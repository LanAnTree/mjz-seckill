package com.lanan.mjzseckill.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.lanan.mjzseckill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/10-17:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetail implements UserDetails {

	private User user;

	private List<String> permissions;

	@JSONField(serialize = false)
	private List<SimpleGrantedAuthority> authorities;

	public UserDetail(User user, List<String> permissions) {
		this.user = user;
		this.permissions = permissions;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (authorities != null) {
			return authorities;
		}
		return permissions.stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getNickname();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
