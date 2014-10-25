package jd.ide.intellij.config;

public class JDPluginComponent {

    public static final JDPluginComponent CONF = new JDPluginComponent();

    private boolean showLineNumbersEnabled = false;
    private boolean showMetadataEnabled = false;
    private boolean escapeUnicodeCharactersEnabled = false;
    private boolean omitPrefixThisEnabled = false;
    private boolean realignLineNumbersEnabled = true;

    private boolean saveToZip = false;


    public boolean isShowLineNumbersEnabled() {
        return showLineNumbersEnabled;
    }

    public boolean isShowMetadataEnabled() {
        return showMetadataEnabled;
    }

    public boolean isEscapeUnicodeCharactersEnabled() {
        return escapeUnicodeCharactersEnabled;
    }

    public boolean isOmitPrefixThisEnabled() {
        return omitPrefixThisEnabled;
    }

    public boolean isRealignLineNumbersEnabled() {
        return realignLineNumbersEnabled;
    }

    public void setShowLineNumbersEnabled(boolean showLineNumbersEnabled) {
        this.showLineNumbersEnabled = showLineNumbersEnabled;
    }

    public void setShowMetadataEnabled(boolean showMetadataEnabled) {
        this.showMetadataEnabled = showMetadataEnabled;
    }

    public void setEscapeUnicodeCharactersEnabled(boolean escapeUnicodeCharactersEnabled) {
        this.escapeUnicodeCharactersEnabled = escapeUnicodeCharactersEnabled;
    }

    public void setOmitPrefixThisEnabled(boolean omitPrefixThisEnabled) {
        this.omitPrefixThisEnabled = omitPrefixThisEnabled;
    }

    public void setRealignLineNumbersEnabled(boolean realignLineNumbersEnabled) {
        this.realignLineNumbersEnabled = realignLineNumbersEnabled;
    }

    public boolean isSaveToZip() {
        return saveToZip;
    }

    public void setSaveToZip(boolean saveToZip) {
        this.saveToZip = saveToZip;
    }
}
