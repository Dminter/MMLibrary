package com.zncm.library.utils;

/**
 * Created by jiaomx on 2017/6/15.
 */


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * 在线性时间内抽取主题类（新闻、博客等）网页的正文。 采用了<b>基于行块分布函数</b>的方法，为保持通用性没有针对特定网站编写规则。
 * 核心代码参考网络上陈鑫所写的TextExtract.java编写而成，加入了高亮显示的功能。
 * </p>
 *
 * @author 倪庆洋
 * @version 1.0, 2013-08-05
 */
public class TextExtractor {

    private final int blocksWidth;
    private int threshold;

    private static final Pattern startPattern = Pattern.compile("^[0-9\u4e00-\u9fa5].+?");
    private static final Pattern endPattern = Pattern.compile(".+([0-9\u4e00-\u9fa5，。！？])$+?");

    public TextExtractor() {
        this.blocksWidth = 3;
        /* 当待抽取的网页正文中遇到成块的新闻标题未剔除时，只要增大此阈值即可。 */
		/* 阈值增大，准确率提升，召回率下降；值变小，噪声会大，但可以保证抽到只有一句话的正文 */
        this.threshold = 86;
    }

    public void setThreshold(int value) {
        this.threshold = value;
    }

    public String extract(String html) {
        StringBuilder text = new StringBuilder();
        for (TextLine textLine : this.parse(html)) {
            if (textLine.isContent()) {
                text.append(textLine.getLineText());
                text.append("\n");
            }
        }
        return text.toString();
    }


    private String preProcess(String html) {
        html = html.replaceAll("(?is)<!DOCTYPE.*?>", "");
        html = html.replaceAll("(?is)<!--.*?-->", ""); // remove html comment
        html = html.replaceAll("(?is)<script.*?>.*?</script>", ""); // remove
        // javascript
        html = html.replaceAll("(?is)<style.*?>.*?</style>", ""); // remove css
        html = html.replaceAll("&.{2,5};|&#.{2,5};", " "); // remove special
        // char
        html = html.replaceAll("(?is)<.*?>", "");
        // <!--[if !IE]>|xGv00|9900d21eb16fa4350a3001b3974a9415<![endif]-->
        return html;
    }

    private List<TextLine> parse(String url) {
        Document doc = null;
        try {
             doc = Jsoup.connect(url).header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0").timeout(5000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String    html = doc.html();
        //移除注释
        html = html.replaceAll("(?is)<!--.*?-->", ""); // remove html comment

        //格式化
        html = Jsoup.parse(html).html();
        List<String> htmlLines = Arrays.asList(html.split("\n"));

        List<TextLine> lines = new ArrayList<TextLine>();

        for (int i = 0; i < htmlLines.size(); i++) {

            boolean flag = false;

            TextLine textLine = new TextLine();

            String htmlLine = htmlLines.get(i);

            if (htmlLine == null) {
                flag = true;
                textLine.setLineHtml("");
                textLine.setLineText("");
            } else {
                Element body = Jsoup.parse(htmlLine).body();
                if (body == null) {
                    flag = true;
                    textLine.setLineHtml("");
                    textLine.setLineText("");
                } else {
                    String content = body.html().trim();

                    if ((content.startsWith("<") || (startPattern.matcher(content).matches() || endPattern.matcher(content).matches()))) {
                        flag = true;
                        textLine.setLineHtml(htmlLines.get(i));
                        textLine.setLineText(this.preProcess(content));
                    }
                }
            }

            if (flag) {
                textLine.setLineNumber(i);
                textLine.setContent(false);
                lines.add(textLine);
            }

        }

        ArrayList<Integer> indexDistribution = new ArrayList<Integer>();

        for (int i = 0; i < lines.size() - blocksWidth; i++) {
            int wordsNum = 0;
            for (int j = i; j < i + blocksWidth; j++) {
                TextLine textLine = lines.get(j);
                String text = textLine.getLineText().replaceAll("\\s+", "");
                textLine.setLineText(text);
                wordsNum += text.length();
            }
            indexDistribution.add(wordsNum);
            // System.out.println(wordsNum);
        }

        int start = -1;
        int end = -1;
        boolean boolstart = false, boolend = false;

        for (int i = 0; i < indexDistribution.size() - 1; i++) {
            if (indexDistribution.get(i) > threshold && !boolstart) {
                if (indexDistribution.get(i + 1).intValue() != 0 || indexDistribution.get(i + 2).intValue() != 0 || indexDistribution.get(i + 3).intValue() != 0) {
                    boolstart = true;
                    start = i;
                    continue;
                }
            }
            if (boolstart) {
                if (indexDistribution.get(i).intValue() == 0 || indexDistribution.get(i + 1).intValue() == 0) {
                    end = i;
                    boolend = true;
                }
            }
            if (boolend) {
                StringBuilder tmp = new StringBuilder();
                // System.out.println(start+1 + "\t\t" + end+1);
                List<Integer> tempList = new ArrayList<Integer>();
                for (int ii = start; ii <= end; ii++) {
                    if (lines.get(ii).getLineText().length() < 5) {
                        continue;
                    }
                    tmp.append(lines.get(ii).getLineText());
                    tmp.append("\n");
                    tempList.add(ii);
                }
                String str = tmp.toString();
//				System.out.println(str);
//				System.out.println("--------------------------------");
                if (str.contains("Copyright") && str.contains("版权所有")) {
                    continue;
                }
                for (int j = 0; j < tempList.size(); j++) {
                    lines.get(tempList.get(j)).setContent(true);
                }
                boolstart = boolend = false;
            }
        }

        return lines;
    }

    public class TextLine {

        private int lineNumber = 0;
        // 行内容,未经处理过的带标签的行
        private String lineHtml = null;
        private String lineText = null;
        // 判断是否为内容
        private boolean isContent = false;
        ;

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public String getLineHtml() {
            return lineHtml;
        }

        public void setLineHtml(String lineHtml) {
            this.lineHtml = lineHtml;
        }

        public String getLineText() {
            return lineText;
        }

        public void setLineText(String lineText) {
            this.lineText = lineText;
        }

        public boolean isContent() {
            return isContent;
        }

        public void setContent(boolean isContent) {
            this.isContent = isContent;
        }

    }

    public static void main(String[] args) throws Exception {

        String url = "http://www.3lian.com/zl/2016/06-20/1466390175460735.html";

        //此处缺失的类，自己写即可，只要读取出html内容就可以进行测试
//        byte[] data = HtmlUtil.readURLToBytes(url);
//        String html = EncodingUtil.toString(data);

        String content = new TextExtractor().extract(url);
        System.out.println(content);
    }
}