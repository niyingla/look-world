package coding.java8plus;

import org.junit.Test;

import java.time.*;

public class Local {

    @Test
    public void test_date(){
        // 2022年2月22日
        LocalDate date = LocalDate.of(2022, 2, 22);

        // 10:55:59
        LocalTime time = LocalTime.of(10, 55, 59);

        // 当前时间
        LocalDateTime datetime = LocalDateTime.of(date, time);

        ZonedDateTime zoneDT = ZonedDateTime.of(datetime, ZoneId.of("Asia/Shanghai"));
    }
}
