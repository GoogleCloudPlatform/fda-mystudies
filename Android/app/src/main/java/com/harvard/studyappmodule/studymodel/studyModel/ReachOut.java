package com.harvard.studyappmodule.studymodel;

import io.realm.RealmObject;

/**
 * Created by Rohit on 2/28/2017.
 * Study list data
 */

public class ReachOut extends RealmObject {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
