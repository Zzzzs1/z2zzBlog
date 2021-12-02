package blog.vo;


import blog.domain.Catalog;

/**
 * Catalog VO.
 *
 * @author <a href="https://waylau.com">Way Lau</a>
 * @since 1.0.0 2017年8月1日
 */
public class CatalogVO {

    private String username;
    private Catalog catalog;

    public CatalogVO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

}
