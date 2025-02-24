package org.sunyata.game; /**
 * Created by leo on 17/4/18.
 */

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.sunyata.game.edy.state.manager.config.EnableGameServer;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableScheduling
//@EnableQuarkClient
//@EnableThriftClient
@EnableGameServer
public class Application {

//    @Autowired
//    BusinessManager businessManager;

    public static void main(String[] args) throws InterruptedException {
        new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE).run(args);
    }

    //
//    @Bean
//    public AlwaysSampler defaultSampler() {
//        return new AlwaysSampler();
//    }
//    @PostConstruct
//    public void initCoordination() {
//
//    }

//    @Bean
//    BusinessManager businessManager() throws Exception {
//        BusinessManager c = new MultipleThreadBusinessManager();
//        c.setScanPackage("org.sunyata.quark.embed.demo.springcloud.components");
//        c.setServiceLocator(SpringServiceLocator.class);
//        c.setEventPublisher(SpringEventEventPublisher.class);
//        c.initialize();
////        c.register(ParallelBusinessComponent.class);
////        c.register(SingleBusinessComponent.class);
////        c.register(TwoBusinessComponent.class);
//        return c;
//    }
}
