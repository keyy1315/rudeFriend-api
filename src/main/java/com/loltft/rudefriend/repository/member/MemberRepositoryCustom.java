package com.loltft.rudefriend.repository.member;

import com.loltft.rudefriend.dto.enums.DateOption;
import com.loltft.rudefriend.dto.enums.GameSelectOption;
import com.loltft.rudefriend.dto.member.MemberResponse;
import com.loltft.rudefriend.entity.enums.Role;
import com.loltft.rudefriend.entity.enums.Tier;
import java.time.LocalDateTime;
import java.util.List;

public interface MemberRepositoryCustom {

  List<MemberResponse> findAllByOption(
      String search,
      GameSelectOption option,
      Tier tier,
      Boolean status,
      Role role,
      LocalDateTime localDateTime,
      LocalDateTime localDateTime1,
      DateOption dateOption,
      Integer pageNo);

  Long countAllByOption(
      String search,
      GameSelectOption option,
      Tier tier,
      Boolean status,
      Role role,
      LocalDateTime localDateTime,
      LocalDateTime localDateTime1,
      DateOption dateOption);
}
