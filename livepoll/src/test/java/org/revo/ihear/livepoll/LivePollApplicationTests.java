package org.revo.ihear.livepoll;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.revo.base.domain.Stream;
import org.revo.ihear.livepoll.config.rtspHandler.HolderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LivePollApplicationTests {
    @Autowired
    private HolderImpl holderImpl;

    @Test
    public void contextLoads() {
//        holderImpl.getStreamService().save(new Stream());
    }

}
