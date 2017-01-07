package com.zncm.library.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TextExtractor {

    private final int blocksWidth;
    private int threshold;

    private static final Pattern startPattern = Pattern.compile("^[0-9\u4e00-\u9fa5].+?");
    private static final Pattern endPattern = Pattern.compile(".+([0-9\u4e00-\u9fa5，。！？])$+?");

    public TextExtractor() {
        this.blocksWidth = 3;
        this.threshold = 86;
    }

    public void setThreshold(int value) {
        this.threshold = value;
    }

    /**
     * 提取正文，判断传入HTML，若是主题类网页，则抽取正文
     *
     * @param html 网页HTML字符串
     * @return 网页正文string
     */
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


    /**
     * 高亮显示
     *
     * @param html 网页原文
     * @return 加入高亮显示后的网页原文
     */
    public String highLighter(String html) {

        List<TextLine> lines = this.parse(html);

        //移除注释
        html = html.replaceAll("(?is)<!--.*?-->", ""); // remove html comment
        //格式化
        html = Jsoup.parse(html).html();

        List<String> htmlLines = Arrays.asList(html.split("\n"));
        for (TextLine textLine : lines) {
            if (textLine.getLineNumber() >= htmlLines.size()) {
                continue;
            }
            if (textLine.isContent()) {
                Element bodyElement = Jsoup.parse(textLine.getLineHtml()).body();
                if (!textLine.getLineHtml().trim().startsWith("<") || !textLine.getLineHtml().trim().endsWith(">")) {
                    bodyElement.html("<span class='x-boilerpipe-mark1'>" + bodyElement.html() + "<span>");
                } else {
                    for (Element element : bodyElement.children()) {
                        element.html("<span class='x-boilerpipe-mark1'>" + element.html() + "</span>");
                    }
                }
                htmlLines.set(textLine.getLineNumber(), bodyElement.html());
            }

        }
        StringBuilder text = new StringBuilder();
        for (String htmlLine : htmlLines) {
            text.append(htmlLine);
            text.append("\n");
        }
        Document document = Jsoup.parse(text.toString());
        document.head().prepend("<style type='text/css'>.x-boilerpipe-mark1 { text-decoration:none; background-color: #ffff42 !important; color: black !important; visibility:visible !important;}</style>");
        return document.html();
    }

    /**
     * 预处理
     *
     * @param html
     * @return
     */
    private String preProcess(String html) {
        html = html.replaceAll("(?is)<!DOCTYPE.*?>", "");
        html = html.replaceAll("(?is)<!--.*?-->", ""); // remove html comment
        html = html.replaceAll("(?is)<script.*?>.*?</script>", ""); // remove
        // javascript
        html = html.replaceAll("(?is)<style.*?>.*?</style>", ""); // remove css
        html = html.replaceAll("&.{2,5};|&#.{2,5};", " "); // remove special
        // char
        html = html.replaceAll("(?is)<.*?>", "");
        return html;
    }

    /**
     * 判断传入HTML，若是主题类网页，则抽取正文；否则输出<b>"unkown"</b>。
     *
     * @return 网页正文string
     */
    private List<TextLine> parse(String html) {
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
                    if ((XUtil.notEmptyOrNull(this.preProcess(content)) || content.startsWith("<") || (startPattern.matcher(content).matches() || endPattern.matcher(content).matches()))) {
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
        private String lineHtml = null;
        private String lineText = null;
        private boolean isContent = false;

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
//
//    public static void main(String[] args) throws Exception {
//        String url = "http://sspai.com/32227";
//        Document doc;
//        doc = Jsoup.connect(url).timeout(1000).get();
//        String html = doc.html();
//        String content = new TextExtractor().extract(html);
//        System.out.println(content);
//    }
}