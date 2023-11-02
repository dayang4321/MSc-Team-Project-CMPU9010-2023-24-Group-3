package com.docparser.springboot.model;

import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.utils.ImmutableMap;

import java.util.List;
import java.util.Map;

import software.amazon.awssdk.enhanced.dynamodb.*;

import java.util.*;

// ... (other necessary imports)

public class CustomAttributeConverterProvider implements AttributeConverterProvider {

    private final Map<EnhancedType<?>, AttributeConverter<?>> converterCache = ImmutableMap.of(
            EnhancedType.of(FeedBackForm.class), new FeedBackFormConverter(),
            EnhancedType.listOf(FeedBackForm.class), new FeedBackFormListConverter()
    );

    public static CustomAttributeConverterProvider create() {
        return new CustomAttributeConverterProvider();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> AttributeConverter<T> converterFor(EnhancedType<T> enhancedType) {
        return (AttributeConverter<T>) converterCache.getOrDefault(enhancedType, null);
    }

    private static class FeedBackFormConverter implements AttributeConverter<FeedBackForm> {
        @Override
        public AttributeValue transformFrom(FeedBackForm form) {
            return AttributeValue.builder()
                    .m(Map.of(
                            "feedback", AttributeValue.builder().s(form.getMessage()).build()
                    ))
                    .build();
        }

        @Override
        public FeedBackForm transformTo(AttributeValue attributeValue) {
            FeedBackForm form = new FeedBackForm();
            form.setMessage(attributeValue.m().get("message").s());
            return form;
        }

        @Override
        public EnhancedType<FeedBackForm> type() {
            return EnhancedType.of(FeedBackForm.class);
        }

        @Override
        public AttributeValueType attributeValueType() {
            return AttributeValueType.M;
        }
    }

    private static class FeedBackFormListConverter implements AttributeConverter<List<FeedBackForm>> {
        private final FeedBackFormConverter formConverter = new FeedBackFormConverter();

        @Override
        public AttributeValue transformFrom(List<FeedBackForm> formList) {
            List<AttributeValue> formAttributeValueList = new ArrayList<>();
            for (FeedBackForm form : formList) {
                formAttributeValueList.add(formConverter.transformFrom(form));
            }
            return AttributeValue.builder().l(formAttributeValueList).build();
        }

        @Override
        public List<FeedBackForm> transformTo(AttributeValue attributeValue) {
            List<FeedBackForm> formList = new ArrayList<>();
            for (AttributeValue av : attributeValue.l()) {
                formList.add(formConverter.transformTo(av));
            }
            return formList;
        }

        @Override
        public EnhancedType<List<FeedBackForm>> type() {
            return EnhancedType.listOf(FeedBackForm.class);
        }

        @Override
        public AttributeValueType attributeValueType() {
            return AttributeValueType.L;
        }
    }
}
