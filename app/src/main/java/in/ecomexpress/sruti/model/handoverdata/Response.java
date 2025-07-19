package in.ecomexpress.sruti.model.handoverdata;

import androidx.room.Embedded;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by  Deepak Sikka
 * Ecom Express private Ltd on 18/9/19.
 */

public class Response
{
    @Embedded
    @JsonProperty("listOfAwbs")
    private List<ListOfAwbs> listOfAwbs;

    public List<ListOfAwbs> getListOfAwbs() {
        return listOfAwbs;
    }

    public void setListOfAwbs(List<ListOfAwbs> listOfAwbs) {
        this.listOfAwbs = listOfAwbs;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [listOfAwbs = "+listOfAwbs+"]";
    }
}

