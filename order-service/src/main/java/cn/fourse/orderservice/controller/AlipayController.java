package cn.fourse.orderservice.controller;

import cn.fourse.orderservice.service.AlipayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay/alipay")
@RequiredArgsConstructor
@RefreshScope
public class AlipayController {

    private final AlipayService alipayService;

    @PostMapping(
            value = "/{orderNo}",
            produces = MediaType.TEXT_HTML_VALUE
    )
    public String pay(
            @PathVariable String orderNo
    ) throws Exception {

        return alipayService.pay(orderNo);
    }

    @PostMapping(
            value = "/notify",
            produces = "text/plain;charset=UTF-8"
    )
    public String notify(
            HttpServletRequest request) {

        try {

            Map<String, String> params =
                    getParams(request);

            return alipayService.handleNotify(params);

        } catch (Exception e) {

            e.printStackTrace();

            return "failure";
        }
    }

    private Map<String, String> getParams(
            HttpServletRequest request) {

        Map<String, String> params =
                new HashMap<>();

        request.getParameterMap()
                .forEach((key, values) ->
                        params.put(
                                key,
                                String.join(",", values)
                        )
                );

        return params;
    }

    @GetMapping("/query/{orderNo}")
    public String query(
            @PathVariable String orderNo
    ) throws Exception {

        return alipayService.query(orderNo);
    }
}