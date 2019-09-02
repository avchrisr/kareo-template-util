package com.chrisr.template_util.repository.oracle;

import com.chrisr.template_util.exception.ResourceNotFoundException;
import com.chrisr.template_util.repository.TemplateRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemplateRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @InjectMocks
    private TemplateRepository templateRepository;


    @Test(expected = ResourceNotFoundException.class)
    public void getUserId_RuntimeExceptionDuringSQL_ShouldThrowResourceNotFoundException() {
        when(namedParameterJdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), any(Class.class))).thenThrow(new RuntimeException("Runtime Exception!"));
        Long id = templateRepository.getUserId("chrisr");
    }

    @Test
    public void getUserId_QueryReturnsLongId_ShouldSucceed() {
        when(namedParameterJdbcTemplate.queryForObject(anyString(), any(MapSqlParameterSource.class), any(Class.class))).thenReturn(50L);
        Long id = templateRepository.getUserId("chrisr");
        assertEquals(50, id.longValue());
    }
}
