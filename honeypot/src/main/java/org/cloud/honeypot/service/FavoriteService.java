package org.cloud.honeypot.service;

import org.cloud.honeypot.dto.FavoriteDto;
import java.util.List;

public interface FavoriteService {
    boolean toggleFavorite(Long memberId, Long complexId);
    boolean isFavorite(Long memberId, Long complexId);
    List<FavoriteDto> getFavorites(Long memberId);
}