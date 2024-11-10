package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.model.Share;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.ShareMapper;
import com.example.sharesnotesapp.model.dto.response.ShareResponseDto;
import com.example.sharesnotesapp.service.share.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/share")
public class ShareController {
    private final ShareService shareService;
    private final ShareMapper mapper;

    @Autowired
    public ShareController(ShareService shareService, ShareMapper mapper) {
        this.shareService = shareService;
        this.mapper = mapper;
    }

    @PostMapping("/{noteId}")
    public ResponseEntity<ShareResponseDto> shareNote(@PathVariable Long noteId, @RequestBody String receiverEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            URI uri = URI.create((ServletUriComponentsBuilder.fromCurrentContextPath().path("/share").toUriString()));
            Share share = shareService.shareNote(user, receiverEmail, noteId);

            return ResponseEntity.created(uri).body(mapper.toDto(share));
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShareResponseDto> getShareById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            Share share = shareService.getShareById(id);

            return ResponseEntity.ok(mapper.toDto(share));
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/sent")
    public ResponseEntity<List<ShareResponseDto>> getSharedNotesBetweenUsers(@RequestParam(defaultValue = "") String receiverEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            List<Share> shareList = shareService.getAllSharedNotesBetweenUsers(user, receiverEmail);

            return ResponseEntity.ok(shareList.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/received")
    public ResponseEntity<List<ShareResponseDto>> getReceivedNotesBetweenUsers(@RequestParam(defaultValue = "") String senderEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            List<Share> shareList = shareService.getAllReceivedNotesBetweenUsers(user, senderEmail);

            return ResponseEntity.ok(shareList.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }
}

