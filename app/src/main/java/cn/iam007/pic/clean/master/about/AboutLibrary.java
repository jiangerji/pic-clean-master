package cn.iam007.pic.clean.master.about;

public class AboutLibrary implements Comparable<AboutLibrary> {
    /*
    <?xml version="1.0" encoding="utf-8"?>
    <resources>

    <string name="define_AboutLibraries">year;owner</string>
    <string name="library_AboutLibraries_author">Mike Penz</string>
    <string name="library_AboutLibraries_authorWebsite">http://mikepenz.com/</string>
    <string name="library_AboutLibraries_libraryName">AboutLibraries</string>
    <string name="library_AboutLibraries_libraryDescription">
    <![CDATA[
    <b>AboutLibraries</b> is a library to offer you all the information you need of your libraries!
    <br /><br />
    Most modern apps feature an "Used Library"-Section and for this some information of those libs is required. As it gets annoying to copy those strings always to your app I have developed this small helper library to provide the required information.
            ]]>
    </string>
    <string name="library_AboutLibraries_libraryVersion">4.7.2</string>
    <string name="library_AboutLibraries_libraryWebsite">https://github.com/mikepenz/AboutLibraries</string>
    <string name="library_AboutLibraries_licenseId">apache_2_0</string>
    <string name="library_AboutLibraries_isOpenSource">true</string>
    <string name="library_AboutLibraries_repositoryLink">https://github.com/mikepenz/AboutLibraries</string>
    <!-- Custom variables section -->
    <string name="library_AboutLibraries_owner">Mike Penz</string>
    <string name="library_AboutLibraries_year">2014</string>
    </resources>
    */

    private String definedName = "";

    // 作者
    private String author = "";

    // 作者的网址
    private String authorWebsite = "";

    // 引用库的名字
    private String libraryName = "";

    // 引用库的描述
    private String libraryDescription = "";

    // 引用库的版本
    private String libraryVersion = "";

    // 引用库的地址
    private String libraryWebsite = "";

    // 是否是开源库
    private boolean isOpenSource = true;

    // 开源库的地址
    private String repositoryLink = "";

    /**
     * 构造AboutLibrary对象
     *
     * @param author             使用库的作者
     * @param libraryName        使用库的名称
     * @param libraryDescription 使用库的描述
     */
    public AboutLibrary(String author, String libraryName, String libraryDescription) {
        this.author = author;
        this.libraryName = libraryName;
        this.libraryDescription = libraryDescription;
    }

    public String getDefinedName() {
        return definedName;
    }

    public void setDefinedName(String definedName) {
        this.definedName = definedName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorWebsite() {
        return authorWebsite;
    }

    public void setAuthorWebsite(String authorWebsite) {
        this.authorWebsite = authorWebsite;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getLibraryDescription() {
        return libraryDescription;
    }

    public void setLibraryDescription(String libraryDescription) {
        this.libraryDescription = libraryDescription;
    }

    public String getLibraryVersion() {
        return libraryVersion;
    }

    public void setLibraryVersion(String libraryVersion) {
        this.libraryVersion = libraryVersion;
    }

    public String getLibraryWebsite() {
        return libraryWebsite;
    }

    public void setLibraryWebsite(String libraryWebsite) {
        this.libraryWebsite = libraryWebsite;
    }

    public boolean isOpenSource() {
        return isOpenSource;
    }

    public void setOpenSource(boolean isOpenSource) {
        this.isOpenSource = isOpenSource;
    }

    public String getRepositoryLink() {
        return repositoryLink;
    }

    public void setRepositoryLink(String repositoryLink) {
        this.repositoryLink = repositoryLink;
    }

    @Override
    public int compareTo(AboutLibrary another) {
        return getLibraryName().compareToIgnoreCase(another.getLibraryName());
    }
}
