package org.trenders

class WikipediaService {
	def financeService // TODO: separate this, they're not really related
	
    def getURL(def title) {
        title = URLEncoder.encode(title.replace(" ", "_"))
        "http://en.wikipedia.org/wiki/$title"
    }
    
    def getArticleSource(def title) {
        try {
            def url = getURL(title)
            def content = url.toURL().text
            def source = between(content,
                "<div id=\"mw-content-text\" lang=\"en\" dir=\"ltr\" class=\"mw-content-ltr\">",
                "</div>				<!-- /bodycontent -->"
            )
			
			if (source.length() > 0) {
				return source
			}
        } catch (Exception ex) {
            ex.printStackTrace()
        }
		
		def simpleTitle = financeService.getSimpleName(title)
		
		if (simpleTitle != title) {
			return getArticleSource(simpleTitle)
		} else {
			return null
		}
    }
    
    def cleanupHTML(def source) {
        source = source.replaceAll("(?s)\\<table(.*?)\\</table\\>", "")
        source = source.replaceAll("(?s)\\<div class=\"dablink\"\\>(.*?)\\</div\\>", "")
        source = source.replaceAll("(?s)\\<div class=\"metadata topicon\"(.*?)\\</div\\>", "")
        source = source.replaceAll("(?s)\\<div class=\"thumb tright\"\\>(.*?)</div>(.*?)</div>", "")
        source = source.replaceAll("(?s)\\<div(.*?)\\</div\\>", "")
        source = source.replaceAll("(?s)\\</div\\>", "");
        source = source.replaceAll("(?s)\\<map(.*?)\\</map\\>", "")
        source = source.replaceAll("(?s)\\<img(.*?)/\\>", "")
        source = source.replaceAll("(?s)\\<sup(.*?)\\</sup\\>", "")
        source = source.replaceAll("(?s)\\<p\\>\\<span style=\"font-size: small;\"\\>\\<span id=\"coordinates\"\\>(.*?)\\</span\\>\\</span\\>\\</p\\>", "");
        source = source.replaceAll("href=\"/wiki/", "target=\"_blank\" href=\"http://en.wikipedia.org/wiki/")
        
        return source
    }
    
    def getSummaryFromHTML(def source) {
        // remove everything after a header
        source.replaceAll("(?s)<h(.*)", "")
    }
    
    // TODO: move these to a utility class
    public static String between(String content, String first, String last) {
        return betweenMore(content, first, last, 1);
    }

    public static String betweenMore(String content, String first, String last, int index) {
        try {
            for (int i = 0; i < index; i ++) {
                content = content.substring(content.indexOf(first) + first.length());
            }

            content = content.substring(0, content.indexOf(last));
        } catch (Exception ex) {
            return null;
        }

        return content;
    }
}
