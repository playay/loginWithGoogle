package fr.javatic.mongo.jacksonCodec.javaTime.serializers;

import java.util.Date;

public class NotLongDateSerializer extends InstantSerializerBase<Date> {
    public NotLongDateSerializer() {
        super(Date.class, zdt -> zdt.toInstant().toEpochMilli());
    }
}