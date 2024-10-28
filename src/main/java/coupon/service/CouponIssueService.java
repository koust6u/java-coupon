package coupon.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import coupon.domain.coupon.Coupon;
import coupon.domain.coupon.CouponIssuer;
import coupon.domain.member.Member;
import coupon.domain.repository.CouponRepository;
import coupon.domain.repository.InMemoryCouponRepository;
import coupon.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponIssueService {

    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;

    @Transactional
    public void issueNewCoupon(final long memberId, final long couponId) {
        final Member member = memberRepository.fetchById(memberId);
        final Coupon coupon = couponRepository.fetchById(couponId);
        final List<Coupon> issuedCoupons = couponRepository.fetchByCouponName(coupon.getCouponName());
        issuedCoupons.addAll(InMemoryCouponRepository.getAll(coupon.getCouponName()));
        final CouponIssuer issuer = new CouponIssuer(coupon);
        final Coupon issueCoupon = issuer.issue(member, issuedCoupons);
        InMemoryCouponRepository.insert(issueCoupon);
    }

    public List<Coupon> findAllIssuedCoupon(final long memberId) {
        return InMemoryCouponRepository.getByMemberId(memberId);
    }
}