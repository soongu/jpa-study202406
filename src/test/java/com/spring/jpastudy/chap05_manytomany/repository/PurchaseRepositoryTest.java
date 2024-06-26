package com.spring.jpastudy.chap05_manytomany.repository;

import com.spring.jpastudy.chap05_manytomany.entity.Goods;
import com.spring.jpastudy.chap05_manytomany.entity.Purchase;
import com.spring.jpastudy.chap05_manytomany.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class PurchaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private EntityManager em;

    private User user1;
    private User user2;
    private User user3;
    private Goods goods1;
    private Goods goods2;
    private Goods goods3;

    @BeforeEach
    void setUp() {
        user1 = User.builder().name("망곰이").build();
        user2 = User.builder().name("하츄핑").build();
        user3 = User.builder().name("쿠로미").build();
        goods1 = Goods.builder().name("뚜비모자").build();
        goods2 = Goods.builder().name("닭갈비").build();
        goods3 = Goods.builder().name("중식도").build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        goodsRepository.save(goods1);
        goodsRepository.save(goods2);
        goodsRepository.save(goods3);
    }

    @Test
    @DisplayName("유저와 상품 생성 테스트")
    void createUserAndGoodsTest() {
        User savedUser = userRepository.findById(user1.getId()).orElseThrow();
        Goods savedGoods = goodsRepository.findById(goods1.getId()).orElseThrow();

        assertEquals("망곰이", savedUser.getName());
        assertEquals("뚜비모자", savedGoods.getName());
    }

    @Test
    @DisplayName("유저와 상품을 연결하여 구매 기록 생성 테스트")
    void createPurchaseTest() {
        Purchase purchase = Purchase
                .builder()
                .user(user1)
                .goods(goods1)
                .build();
        purchaseRepository.save(purchase);

        em.flush();
        em.clear();

        Purchase savedPurchase = purchaseRepository.findById(purchase.getId()).orElseThrow();

        System.out.println("\n\n\n구매한 회원정보: " + savedPurchase.getUser() + "\n\n");
        System.out.println("\n\n\n구매한 상품정보: " + savedPurchase.getGoods() + "\n\n");

        assertEquals(user1.getId(), savedPurchase.getUser().getId());
        assertEquals(goods1.getId(), savedPurchase.getGoods().getId());
    }

    @Test
    @DisplayName("특정 유저의 구매 목록 조회 테스트")
    void findPurchasesByUserTest() {
        Purchase purchase1 = Purchase.builder()
                .user(user1).goods(goods1).build();
        Purchase purchase2 = Purchase.builder()
                .user(user1).goods(goods3).build();

        purchaseRepository.save(purchase1);
        purchaseRepository.save(purchase2);

        em.flush();
        em.clear();

        User user = userRepository.findById(user1.getId()).orElseThrow();
        List<Purchase> purchases = user.getPurchases();

        for (Purchase p : purchases) {
            System.out.printf("\n\n%s 님이 구매한 물품명 : %s\n\n",
                    user.getName(), p.getGoods().getName());
        }

        assertEquals(2, purchases.size());
        assertTrue(purchases.stream().anyMatch(p -> p.getGoods().equals(goods1)));
        assertTrue(purchases.stream().anyMatch(p -> p.getGoods().equals(goods3)));
    }

    @Test
    @DisplayName("특정 상품을 구매한 유저 목록 조회 테스트")
    void findUsersByGoodsTest() {
        Purchase purchase1 = Purchase.builder()
                .user(user2).goods(goods1).build();
        Purchase purchase2 = Purchase.builder()
                .user(user3).goods(goods1).build();

        purchaseRepository.save(purchase1);
        purchaseRepository.save(purchase2);

        em.flush();
        em.clear();

        Goods goods = goodsRepository.findById(goods1.getId()).orElseThrow();
        List<Purchase> purchases = goods.getPurchases();

        for (Purchase p : purchases) {
            System.out.printf("\n\n%s 상품을 구매한 유저명 : %s\n\n",
                    goods.getName(), p.getUser().getName());
        }

        assertEquals(2, purchases.size());
        assertTrue(purchases.stream().anyMatch(p -> p.getUser().equals(user2)));
        assertTrue(purchases.stream().anyMatch(p -> p.getUser().equals(user3)));
    }

    @Test
    @DisplayName("구매 기록 삭제 테스트")
    void deletePurchaseTest() {
        Purchase purchase = Purchase.builder()
                .user(user1).goods(goods1).build();

        Purchase savedPurchase = purchaseRepository.save(purchase);

        em.flush();
        em.clear();

        purchaseRepository.deleteById(savedPurchase.getId());

        em.flush();
        em.clear();

        assertFalse(purchaseRepository.findById(purchase.getId()).isPresent());
    }


    @Test
    @DisplayName("회원이 탈퇴하면 구매기록이 삭제되어야 한다")
    void cascadeRemoveTest() {
        //given
        Purchase purchase1 = Purchase.builder()
                .user(user1).goods(goods2).build();

        Purchase purchase2 = Purchase.builder()
                .user(user1).goods(goods3).build();

        Purchase purchase3 = Purchase.builder()
                .user(user2).goods(goods1).build();

        purchaseRepository.save(purchase1);
        purchaseRepository.save(purchase2);
        purchaseRepository.save(purchase3);

        em.flush();
        em.clear();

        User user = userRepository.findById(user1.getId()).orElseThrow();
        List<Purchase> purchases = user.getPurchases();

        System.out.println("\n\nuser1's purchases = " + purchases + "\n\n");
        System.out.println("\n\nall of purchases = " + purchaseRepository.findAll() + "\n\n");

        userRepository.delete(user);

        em.flush();
        em.clear();
        //when

        List<Purchase> purchaseList = purchaseRepository.findAll();

        System.out.println("\n\nafter removing purchaseList = " + purchaseList + "\n\n");

        //then
    }


}