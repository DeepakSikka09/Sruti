package in.ecomexpress.sruti.model.commitdata;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by deepak on 10/10/19.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recci implements Parcelable {
    private String answer;

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    private int question_id;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAnswer () {
        return answer;
    }

    public void setAnswer (String answer) {
        this.answer = answer;
    }



    @Override
    public String toString()
    {
        return "ClassPojo [answer = "+answer+", question_id = "+question_id+"]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.answer);
        dest.writeInt(this.question_id);
        dest.writeString(this.comment);
    }

    public Recci() {
    }

    protected Recci(Parcel in) {
        this.answer = in.readString();
        this.question_id = in.readInt();
        this.comment = in.readString();
    }

    public static final Parcelable.Creator<Recci> CREATOR = new Parcelable.Creator<Recci>() {
        @Override
        public Recci createFromParcel(Parcel source) {
            return new Recci(source);
        }

        @Override
        public Recci[] newArray(int size) {
            return new Recci[size];
        }
    };
}

