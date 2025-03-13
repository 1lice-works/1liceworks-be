package com.elice.iliceworksbe.auth.dto.request;

import com.elice.iliceworksbe.team.constant.Industry;
import com.elice.iliceworksbe.team.constant.Scale;

public record SignUpRequestDto(
    TeamInfo teamInfo,
    UserInfo userInfo
) {
    public record TeamInfo(
            String companyName,
            String teamName,
            Industry industry,
            Scale scale,
            boolean hasPrivateDomain,
            String domainName
    ){}

    public record UserInfo(
            String username,
            String privateEmail,
            String accountId,
            String password
    ){}
}