<p style="text-align: center; Arial, sans-serif; font-size: 36px;">
  🟣Лабораторная №3, ПнаЯВУ  
  🟣группа 334701  
  🟣Вариант №13🟣  
</p>

### `[By Anton Kozlov] 👁‍🗨⭕️👁‍🗨`
---
Полезный `GET` - запрос: получение переводов по пользователю (по user id).
```
public interface TranslationRepository extends JpaRepository<Translation, Long> {
    List<Translation> findByUserId(Long userId);
}
```

Пример запроса для 
```http://localhost:8080/getTranslation?srcL=en&targetL=ru&text=Hello+World```

Модификация в service layer
```
public List<Translation> getTranslationsByUserId(Long userId) {

        List<Translation> cachedTranslations = translationCache.getCachedTranslations(userId);
        if (cachedTranslations != null) return cachedTranslations;

        List<Translation> translations = translationRepository.findByUserId(userId);

        // Поместить в кэш
        translationCache.putTranslations(userId, translations);
        return translations;
    }
  ```

<p style="text-align: center; Arial, sans-serif; font-size: 36px;">
👩🏻‍🦽👩🏻‍🦽👩🏻‍🦽💨💨💨
</p>

