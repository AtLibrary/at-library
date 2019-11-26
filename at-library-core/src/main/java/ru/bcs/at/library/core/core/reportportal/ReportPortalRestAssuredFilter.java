//package ru.bcs.at.library.core.core.reportportal;
//
//
//import io.qameta.allure.attachment.AttachmentData;
//import io.qameta.allure.attachment.AttachmentProcessor;
//import io.qameta.allure.attachment.FreemarkerAttachmentRenderer;
//import io.qameta.allure.attachment.http.HttpRequestAttachment;
//import io.qameta.allure.attachment.http.HttpResponseAttachment;
//import io.restassured.filter.FilterContext;
//import io.restassured.filter.OrderedFilter;
//import io.restassured.http.Headers;
//import io.restassured.internal.NameAndValue;
//import io.restassured.response.Response;
//import io.restassured.specification.FilterableRequestSpecification;
//import io.restassured.specification.FilterableResponseSpecification;
//import lombok.extern.log4j.Log4j2;
//import ru.bcs.at.library.core.core.helpers.LogReportListener;
//import ru.bcs.at.library.core.core.log.DocumentFormatter;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * фильтр RestAssured для прикрепления запросов и ответов к отчету о прогоне в ReportPortal.
// * <p>
// * Инициализируется вызовом {@link LogReportListener#turnOn()} ()} ()} ()} где-нибудь в beforeClass
// */
//@Log4j2
//public class ReportPortalRestAssuredFilter implements OrderedFilter {
//    public static final String ATTACHMENT_LOG_LEVEL = "DEBUG";
//
//    private String requestTemplatePath = "http-rp-request.ftl";
//    private String responseTemplatePath = "http-rp-response.ftl";
//
//    private static Map<String, String> toMapConverter(final Iterable<? extends NameAndValue> items) {
//        final Map<String, String> result = new HashMap<>();
//        items.forEach(h -> result.put(h.getName(), h.getValue()));
//        return result;
//    }
//
//    public void setRequestTemplate(final String templatePath) {
//        this.requestTemplatePath = templatePath;
//    }
//
//    public void setResponseTemplate(final String templatePath) {
//        this.responseTemplatePath = templatePath;
//    }
//
//    @Override
//    public int getOrder() {
//        return Integer.MAX_VALUE;
//    }
//
//    @Override
//    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext filterContext) {
//        final AttachmentProcessor<AttachmentData> processor = createAttachmentProcessor();
//
//        final String requestUrl = requestSpec.getURI();
//        final HttpRequestAttachment.Builder requestAttachmentBuilder = HttpRequestAttachment.Builder
//                .create(makeRequestAttachmentName(requestSpec), requestUrl)
//                .setMethod(requestSpec.getMethod())
//                .setHeaders(toMapConverter(requestSpec.getHeaders()));
//
//        if (requestSpec.getBody() != null) {
//            String formatted = DocumentFormatter.createPrettyPrint(requestSpec.getBody().toString(), requestSpec.getContentType());
//            requestAttachmentBuilder.setBody(formatted);
//        }
//        final HttpRequestAttachment requestAttachment = requestAttachmentBuilder.build();
//        processor.addAttachment(requestAttachment, new FreemarkerAttachmentRenderer(requestTemplatePath));
//
//        Response response = filterContext.next(requestSpec, responseSpec);
//
//        final HttpResponseAttachment.Builder responseAttachmentBuilder = HttpResponseAttachment.Builder
//                .create(makeResponseAttachmentName(response)).setResponseCode(response.getStatusCode())
//                .setHeaders(toMapConverter(response.headers()));
//
//        if (response.getBody() != null) {
//            String formattedResp = DocumentFormatter.createPrettyPrint(response.getBody().asString(), response.getContentType());
//            responseAttachmentBuilder.setBody(formattedResp);
//        }
//
//        final HttpResponseAttachment responseAttachment = responseAttachmentBuilder.build();
//        processor.addAttachment(responseAttachment, new FreemarkerAttachmentRenderer(responseTemplatePath));
//
//        return response;
//    }
//
//    protected AttachmentProcessor<AttachmentData> createAttachmentProcessor() {
//        return new CucumberReportPortalAttachmentProcessor();
//    }
//
//    private String makeResponseAttachmentName(Response response) {
//        return response.getStatusLine();
//    }
//
//    private String makeRequestAttachmentName(FilterableRequestSpecification requestSpec) {
//        StringBuilder sb = new StringBuilder();
//        sb = sb.append(requestSpec.getMethod()).append(" to ").append(requestSpec.getURI());
//        Headers headers = requestSpec.getHeaders();
//        if ((headers != null) && (headers.get("SOAPAction") != null)) { // специальное оформление для SOAP
//            sb = sb.append("::").append(headers.get("SOAPAction").getValue());
//        }
//        return sb.toString();
//    }
//
//
//}
