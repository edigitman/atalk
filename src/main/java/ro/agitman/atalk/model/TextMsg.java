package ro.agitman.atalk.model;

import javax.persistence.*;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by edi on 3/22/2016.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "TextMsg.allOfToday",
                query = "select m from TextMsg m where m.insertDate > :yesterday and m.type = 'msg'")
})
public class TextMsg {

    private Long id;
    private String type;
    private String sender;
    private String text;
    private String date;
    private Date insertDate;
    private List<String> users = new ArrayList<String>();
    private List<TextMsg> todays = new ArrayList<TextMsg>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Temporal(TemporalType.DATE)
    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Transient
    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    @Transient
    public List<TextMsg> getTodays() {
        return todays;
    }

    public void setTodays(List<TextMsg> todays) {
        this.todays = todays;
    }
}
