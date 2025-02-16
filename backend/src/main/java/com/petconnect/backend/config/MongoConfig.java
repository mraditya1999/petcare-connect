package com.petconnect.backend.config;


import com.petconnect.backend.entity.Forum;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
//ForumModelListener class
public class MongoConfig extends AbstractMongoEventListener<Forum> {

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Forum> event) {
        Date now = new Date();
        if (event.getSource().getCreatedAt() == null) {
            event.getSource().setCreatedAt(now);
        }
        event.getSource().setUpdatedAt(now);
    }
}