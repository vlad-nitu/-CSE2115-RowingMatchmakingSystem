package com.example.micro.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.micro.domain.Matching;
import com.example.micro.repositories.MatchingRepository;
import com.example.micro.utils.CompositeKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MatchingServiceImplTest {
    @Mock
    private  MatchingRepository matchingRepository;

    private MatchingServiceImpl matchingServiceImpl;

    @BeforeEach
    void setUp() {
        matchingServiceImpl = new MatchingServiceImpl(matchingRepository);
    }

    @Test
    public void findAllTest() {
        List<Matching> result = new ArrayList<>(List.of(new Matching()));
        when(matchingServiceImpl.findAll()).thenReturn(result);
        assertThat(matchingServiceImpl.findAll()).isEqualTo(result);
    }

    @Test
    public void saveTest() {
        Matching matching = new Matching();
        matchingServiceImpl.save(matching);
        verify(matchingRepository, times(1)).save(matching);
    }

    @Test
    public void findActivitiesByUserIdTest() {
        Optional<List<Matching>> result = Optional.of(new ArrayList<>(List.of(
                new Matching("a", 1L, "a", false),
                new Matching("a", 2L, "a", true))));
        when(matchingRepository.findMatchingsByUserId("a")).thenReturn(result);
        List<Long> sol = List.of(1L);
        assertThat(matchingServiceImpl.findActivitiesByUserId("a")).isEqualTo(sol);
    }

    @Test
    public void deleteByIdTest() {
        CompositeKey key = new CompositeKey("a", 1L, "car");
        matchingServiceImpl.deleteById("a", 1L, "car");
        verify(matchingRepository, times(1)).deleteById(key);
    }

    @Test
    public void checkIdTestPendingTrue() {
        Optional<Matching> result = Optional.of(new Matching("a", 1L, "a", true));
        CompositeKey key = new CompositeKey("a", 1L, "car");
        when(matchingRepository.findById(key)).thenReturn(result);
        assertThat(matchingServiceImpl.checkId("a", 1L, "car")).isTrue();
    }

    @Test
    public void checkIdTestPendingFalse() {
        Optional<Matching> result = Optional.of(new Matching("a", 1L, "a", false));
        CompositeKey key = new CompositeKey("a", 1L, "car");
        when(matchingRepository.findById(key)).thenReturn(result);
        assertThat(matchingServiceImpl.checkId("a", 1L, "car")).isFalse();
    }

    @Test
    public void checkIdTestNonexistent() {
        Optional<Matching> result = Optional.empty();
        CompositeKey key = new CompositeKey("a", 1L, "car");
        when(matchingRepository.findById(key)).thenReturn(result);
        assertThat(matchingServiceImpl.checkId("a", 1L, "car")).isFalse();
    }

    @Test
    public void findMatchingWithPendingFalseTest() {
        Optional<Matching> result = Optional.of(new Matching("a", 1L, "a", false));
        when(matchingRepository.findMatchingByUserIdAndActivityIdAndPending("a", 1L, false)).thenReturn(result);
        assertThat(matchingServiceImpl.findMatchingWithPendingFalse("a", 1L)).isEqualTo(result);
    }

}
