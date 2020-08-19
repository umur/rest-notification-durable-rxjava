package miu.edu.restnotificationdurablerxjava.controller;


import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Single;
import rx.Subscription;
import rx.functions.Func0;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/api/actions")
public class ActionController {

    @GetMapping
    public String callExternalUrlAsync() {
        String url = "http://localhost:8081/api/actions";

        Observable.just(url)
                .map(l -> {
                    RestTemplate restTemplate = new RestTemplate();
                    DataDto dto = new DataDto("umur");
                    HttpEntity<DataDto> entity = new HttpEntity<>(dto);
                    return Observable.just(restTemplate.postForObject(url, entity, DataDto.class));
                })
                .retryWhen(errors -> (Observable<?>)
                        errors
                                .take(5)
                                .delay(1000, TimeUnit.MILLISECONDS)
                                .flatMap(error -> {
                                    System.out.println("trying");
                                    return Observable.just(null);
                                }))
                .subscribe(value -> System.out.println(value));

        return "observables";
    }


}

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
class DataDto {
    private String data;
}
