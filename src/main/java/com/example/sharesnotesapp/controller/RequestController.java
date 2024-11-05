package com.example.sharesnotesapp.controller;

import com.example.sharesnotesapp.model.Request;
import com.example.sharesnotesapp.model.User;
import com.example.sharesnotesapp.model.dto.mapper.RequestMapper;
import com.example.sharesnotesapp.model.dto.request.RequestRequestDto;
import com.example.sharesnotesapp.model.dto.response.RequestResponseDto;
import com.example.sharesnotesapp.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;
    private final RequestMapper mapper;

    @Autowired
    public RequestController(RequestService requestService, RequestMapper mapper){
        this.requestService = requestService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<RequestResponseDto> sendRequest(@RequestBody RequestRequestDto requestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated() && authentication.getPrincipal() instanceof User){
            URI uri = URI.create((ServletUriComponentsBuilder.fromCurrentContextPath().path("/requests").toUriString()));

            return ResponseEntity.created(uri).body(mapper.toDto(requestService.sendRequest(requestDto)));
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRequest(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated() && authentication.getPrincipal() instanceof User){
            requestService.deleteRequest(id);

            return ResponseEntity.ok("Request was deleted");
        }

        return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<RequestResponseDto> acceptRequest(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated() && authentication.getPrincipal() instanceof User){
            requestService.acceptRequest(id);
            Request acceptedRequest = requestService.getRequestById(id);

            return ResponseEntity.ok(mapper.toDto(acceptedRequest));
        }

        return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/{id}/decline")
    public ResponseEntity<RequestResponseDto> declineRequest(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated() && authentication.getPrincipal() instanceof User){
            requestService.declineRequest(id);
            Request declinedRequest = requestService.getRequestById(id);

            return ResponseEntity.ok(mapper.toDto(declinedRequest));
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/sent")
    public ResponseEntity<List<RequestResponseDto>> getSentRequests(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user){
            List<Request> sentRequests = requestService.getSentRequests(user);

            return ResponseEntity.ok(sentRequests.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/received")
    public ResponseEntity<List<RequestResponseDto>> getReceivedRequests(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user){
            List<Request> receivedRequests = requestService.getReceivedRequests(user);

            return ResponseEntity.ok(receivedRequests.stream().map(mapper::toDto).toList());
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestResponseDto> getRequestById(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated() && authentication.getPrincipal() instanceof User){
            Request request = requestService.getRequestById(id);

            return ResponseEntity.ok(mapper.toDto(request));
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/remove-friend/{friendId}")
    public ResponseEntity<String> removeFriendFromList(@PathVariable Long friendId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user){
            requestService.removeFromFriendList(user, friendId);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }
}
