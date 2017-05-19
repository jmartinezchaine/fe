package uy.com.fusion.fe.web.api.document.commons;



import java.util.List;

/**
 * Created by didier on 28/10/16.
 */
public class ResponseListEx<T> {

    private List<T> items;
    private Integer offset = 0;
    private Integer limit = 0;
    private int total = 0;

    public ResponseListEx() {
    }

    public ResponseListEx(List<T> items, Integer offset, Integer limit, int total) {
        this.items = items;
        this.offset = offset;
        this.limit = limit;
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
