package org.cloud.honeypot.service.impl;

import org.cloud.honeypot.dto.FavoriteDto;
import org.cloud.honeypot.mapper.FavoriteMapper;
import org.cloud.honeypot.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public boolean toggleFavorite(Long memberId, Long complexId) {
        if (favoriteMapper.countFavorite(memberId, complexId) > 0) {
            favoriteMapper.deleteFavorite(memberId, complexId);
            return false; // 삭제됨
        } else {
            favoriteMapper.insertFavorite(memberId, complexId);
            return true; // 추가됨
        }
    }

    @Override
    public boolean isFavorite(Long memberId, Long complexId) {
        return favoriteMapper.countFavorite(memberId, complexId) > 0;
    }

    @Override
    public List<FavoriteDto> getFavorites(Long memberId) {
        return favoriteMapper.selectFavoritesByMember(memberId);
    }
}