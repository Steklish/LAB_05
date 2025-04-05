# Лабораторная №3, ПнаЯВУ  
### группа 334701 Вариант №13  

### `[By Anton Kozlov] 👁‍🗨⭕️👁‍🗨`
---
Полезный `GET` - запрос: получение переводов по пользователю (по user id). 
```
 @Query("SELECT t FROM Translation t WHERE t.user.id = :userId")
```


Модификация в service layer с исрпользованием аннотаций (пример):
* `@Cacheable(value = "userTranslations", key = "#userId")`
* `@CacheEvict(value = "userTranslations", key = "#userId")`
* `@CachePut(value = "translations", key = "#id")`
<p style="text-align: center; Arial, sans-serif; font-size: 36px;">
👩🏻‍🦽👩🏻‍🦽👩🏻‍🦽💨💨💨
</p>

