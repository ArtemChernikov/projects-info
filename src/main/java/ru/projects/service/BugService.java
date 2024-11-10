package ru.projects.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.projects.repository.BugRepository;

/**
 * @author Artem Chernikov
 * @version 1.0
 * @since 10.11.2024
 */
@Service
@RequiredArgsConstructor
public class BugService {

    private final BugRepository bugRepository;


}
