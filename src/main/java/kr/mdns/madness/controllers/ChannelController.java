package kr.mdns.madness.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.mdns.madness.domain.ChannelMember;
import kr.mdns.madness.dto.ChannelDto;
import kr.mdns.madness.dto.ChannelJoinRequestDto;
import kr.mdns.madness.dto.ChannelJoinResponseDto;
import kr.mdns.madness.dto.ChannelRequestDto;
import kr.mdns.madness.dto.ChannelResponseDto;
import kr.mdns.madness.response.ApiResponse;
import kr.mdns.madness.security.CustomUserDetails;
import kr.mdns.madness.services.ChannelService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@Validated
@RequestMapping("channel")
@RequiredArgsConstructor
public class ChannelController {
        private final ChannelService channelService;

        @GetMapping("/{publicId}")
        public ResponseEntity<ApiResponse<ChannelResponseDto>> getChannelInfo(
                        @PathVariable String publicId,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {

                ChannelResponseDto response = channelService.getChannel(publicId, userDetails.getId());

                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(new ApiResponse<>(0, "Channel Info", response));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<ChannelResponseDto>> createChannel(@Valid @RequestBody ChannelRequestDto req,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                ChannelResponseDto saved = channelService.createChannel(req, userDetails.getId());
                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(new ApiResponse<>(0, "Channel Created", saved));
        }

        @PostMapping("/join")
        public ResponseEntity<ApiResponse<ChannelJoinResponseDto>> joinChannel(
                        @Valid @RequestBody ChannelJoinRequestDto req,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                ChannelMember channelMember = channelService.joinChannelByPublicId(req.getPublicChannelId(),
                                userDetails.getId());
                ChannelJoinResponseDto response = ChannelJoinResponseDto.builder()
                                .publicChannelId(req.getPublicChannelId())
                                .joinAt(channelMember.getJoinedAt()).build();
                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(new ApiResponse<>(0, "Joined Channel", response));
        }

        @DeleteMapping("/leave/{publicId}")
        public ResponseEntity<ApiResponse<Void>> leaveChannel(
                        @PathVariable String publicId,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                channelService.leave(publicId, userDetails.getId());
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .body(new ApiResponse<>(0, publicId, null));
        }

        @GetMapping("/search")
        public ResponseEntity<ApiResponse<List<ChannelDto>>> listChannels(
                        @RequestParam String keyword,
                        @RequestParam(required = false) String cursor,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "desc") String order) {
                boolean asc = order.equalsIgnoreCase("asc");
                List<ChannelDto> list = channelService.searchChannels(keyword, cursor, size, asc);
                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(ApiResponse.of(0, "", list));
        }

        @GetMapping("/{publicId}/members")
        public ResponseEntity<Void> checkChannelJoin(
                        @PathVariable String publicId,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
                channelService.checkMembership(publicId, userDetails.getId());
                return ResponseEntity.ok().build();
        }

        @GetMapping("/top/participants")
        public ResponseEntity<ApiResponse<List<ChannelDto>>> getTopNParticipantChannels(
                        @RequestParam(required = true) int size) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                .body(ApiResponse.of(0, "", channelService.getTopNParticipantChannels(size)));
        }

        @GetMapping("/top/members")
        public ResponseEntity<ApiResponse<List<ChannelDto>>> getTopNMemberChannels(
                        @RequestParam(required = true) int size) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                .body(ApiResponse.of(0, "", channelService.getTopMemberJoinedChannels(size)));
        }

}
