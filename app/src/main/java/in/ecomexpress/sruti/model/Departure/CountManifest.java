package in.ecomexpress.sruti.model.Departure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountManifest {
    private long total;
    private long pendingtotal;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPendingtotal() {
        return pendingtotal;
    }

    public void setPendingtotal(long pendingtotal) {
        this.pendingtotal = pendingtotal;
    }

}
