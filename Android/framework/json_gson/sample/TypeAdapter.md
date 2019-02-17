### TypeAdapter  
先看数据类
```
data class UserEntity(
        var name: String = "",
        val hobbitList: List<String> = listOf(),
        val enLevel: String = "",
        val address: AddressEntity = AddressEntity(),
        val skillEntity: SkillEntity = SkillEntity()
)
```
有时候，会传 完整的JsonObject  {}， 有的时候，只传 name "";   
UserTypeAdapter  
```
public class UserTypeAdapter extends TypeAdapter<UserEntity> {
    private final Gson gson = new Gson();

    @Override
    public void write(JsonWriter out, UserEntity value) throws IOException {
        out.beginObject();
        out.endObject();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public UserEntity read(JsonReader in) throws IOException {
        UserEntity userEntity = new UserEntity();
        try {
            JsonToken jsonToken = in.peek();
            if (JsonToken.BEGIN_OBJECT != jsonToken) {
                return userEntity;
            }
            in.beginObject();
        } catch (Exception ex) {
            LogTrack.w(ex.getMessage());
        }

        while (in.hasNext()) {
            try {
                /*
                 * in.nextName() 要 比  in.peek() 先执行； 负责会死循环
                 * jsonToken 要匹配完， 否则也会死循环；
                 * */
                String nextName = in.nextName();
                JsonToken jsonToken = in.peek();
                if (JsonToken.STRING == jsonToken) {
                    Field field = UserEntity.class.getDeclaredField(nextName);
                    String value = in.nextString();
                    field.setAccessible(true);
                    if ("address".equals(nextName)) {
                        AddressEntity addressEntity = new AddressEntity();
                        addressEntity.setProvince(value);
                        field.set(userEntity, addressEntity);
                    } else {
                        field.set(userEntity, value);
                    }
                } else if (JsonToken.BEGIN_OBJECT == jsonToken) {
                    JsonElement element = Streams.parse(in);
                    Field field = UserEntity.class.getDeclaredField(nextName);
                    field.setAccessible(true);
                    Object value = gson.fromJson(element, field.getGenericType());
                    field.set(userEntity, value);
                } else if (JsonToken.BEGIN_ARRAY == jsonToken) {
                    JsonElement element = Streams.parse(in);
                    Field field = UserEntity.class.getDeclaredField(nextName);
                    field.setAccessible(true);
                    Object value = gson.fromJson(element, field.getGenericType());
                    field.set(userEntity, value);
                }
                LogTrack.w("nextName = " + nextName);
            } catch (Exception ex) {
                LogTrack.w(ex.getMessage());
            }

        }
        in.endObject();
        return userEntity;
    }
}

```

```
@JsonAdapter(UserTypeAdapter::class)
data class UserEntity(
        var name: String = "",
        val hobbitList: List<String> = listOf(),
        val enLevel: String = "",
        val address: AddressEntity = AddressEntity(),
        val skillEntity: SkillEntity = SkillEntity()
)
```

  