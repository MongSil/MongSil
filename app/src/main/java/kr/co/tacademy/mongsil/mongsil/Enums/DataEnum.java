package kr.co.tacademy.mongsil.mongsil.Enums;

import java.util.ArrayList;

/**
 * Created by Han on 2017-02-20.
 */

public enum DataEnum {
    WEATHER_DATA,
    STRING_DATA,
    USER_DATA,
    POST_DATA,
    REPLY_DATA;

    private TypeEnum typeEnum;

    public DataEnum setTypeEnum(TypeEnum typeEnum) {
        this.typeEnum = typeEnum;
        return this;
    }

    public TypeEnum getTypeEnum() {
        return typeEnum;
    }

    public boolean isTypeEnum() {
        return this.typeEnum != null;
    }

    public enum TypeEnum {
        // STRING_DATA's types
        RE_GEO, EDIT_POST, REMOVE_REPLY, REMOVE_POST
    }
}
