package uy.com.fusion.library.rest.serializers.json;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public class SupportedDatesDateFormat
    extends DateFormat {
    private static final long serialVersionUID = -5464750485498359943L;

    private DateFormat formatter = new ISO8601DateFormat();

    public SupportedDatesDateFormat(DateFormat formatter) {
        this.formatter = formatter;
    }

    @Override
    public StringBuffer format(Date date, StringBuffer stringBuffer, FieldPosition fieldPosition) {
        return this.formatter.format(date, stringBuffer, fieldPosition);
    }

    @Override
    public Date parse(String s, ParsePosition parsePosition) {

        try {
            Date d = this.formatter.parse(s);
            parsePosition.setIndex(parsePosition.getIndex() + s.length());
            return d;
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format(
                "Can't parse %s as Date. Expected formats are 'yyyy-MM-dd' and 'yyyy-MM-ddThh:mm:ssZ' ", s), ex);
        }
    }

    @Override
    public Object clone() {
        return new SupportedDatesDateFormat(this.formatter);
    }
}
