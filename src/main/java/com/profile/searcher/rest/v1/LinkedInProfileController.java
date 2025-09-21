package com.profile.searcher.rest.v1;

import com.profile.searcher.model.request.LinkedInProfileSearchDTO;
import com.profile.searcher.model.response.AlumniVO;
import com.profile.searcher.model.response.SuccessResponseVO;
import com.profile.searcher.service.LinkedInSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/linked-in")
@RequiredArgsConstructor
@Validated
public class LinkedInProfileController {

    private final LinkedInSearchService linkedInSearchService;

    @PostMapping("/search")
    public ResponseEntity<SuccessResponseVO<Object>> searchAlumniLinkedInProfiles(
            @RequestBody @Valid LinkedInProfileSearchDTO linkedInProfileSearchDTO) {
        return ResponseEntity.ok(linkedInSearchService.searchAlumniLinkedInProfiles(linkedInProfileSearchDTO));
    }

    @GetMapping("/fetch/{trackingId}")
    public ResponseEntity<SuccessResponseVO<Object>> fetchScrapedAlumniProfiles(@PathVariable UUID trackingId) {
        return ResponseEntity.ok(linkedInSearchService.fetchScrapedAlumniLinkedInProfiles(trackingId));
    }

    @GetMapping("/fetch/all")
    public ResponseEntity<SuccessResponseVO<List<AlumniVO>>> fetchAllAlumni(@RequestParam int page,
                                                                            @RequestParam int limit) {
        return ResponseEntity.ok(linkedInSearchService.fetchAllAlumni(page, limit));
    }
}
