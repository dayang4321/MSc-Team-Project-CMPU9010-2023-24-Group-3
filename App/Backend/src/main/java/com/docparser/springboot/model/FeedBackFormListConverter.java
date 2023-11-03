package com.docparser.springboot.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;

public class FeedBackFormListConverter implements AttributeConverter<List<FeedBackForm>> {

    @Override
    public AttributeValue transformFrom(List<FeedBackForm> feedBackForms) {
        // Implement serialization logic
        return AttributeValue.builder()
                .s(feedBackForms == null ? null : new Gson().toJson(feedBackForms))
                .build();
    }

    @Override
    public List<FeedBackForm> transformTo(AttributeValue attributeValue) {
        // Implement deserialization logic
        return attributeValue.s() == null ? null :
                new Gson().fromJson(attributeValue.s(), new TypeToken<List<FeedBackForm>>(){}.getType());
    }

    @Override
    public EnhancedType<List<FeedBackForm>> type() {
        return (EnhancedType<List<FeedBackForm>>) EnhancedType.of(new TypeToken<List<FeedBackForm>>(){}.getType());
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S; // Because we are storing as a JSON string
    }
}

