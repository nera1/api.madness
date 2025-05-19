package kr.mdns.madness.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DuplicateCheckResponseDto {
    /*
     * field에 is가 들어가면 값이 제대로 Mapping되지 않을 가능성이 있어
     * JSON key 값만 is가 붙도록 조정
     */
    @JsonProperty("isDuplicate")
    private boolean duplicate;
}
