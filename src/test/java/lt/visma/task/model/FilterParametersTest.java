package lt.visma.task.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilterParametersTest {

    @Test
    void builder() {
        String desc = "java";
        FilterParameters filter = FilterParameters.builder().description(desc).build();
        assertEquals(desc,filter.getDescription());
        assertNull(filter.getCategory());
    }
}