package org.hsw.wikitools.feature.copy_skull_id;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GetSkullIdHandlerTest {
    private final Optional<Skull> mockSkullItem =
        Optional.of(Skull.ofTextureValue("ewogICJ0aW1lc3RhbXAiIDogMTYyNDYyMzE3NjM1MywKICAicHJvZmlsZUlkIiA6ICIxYWZhZjc2NWI1ZGY0NjA3YmY3ZjY1ZGYzYWIwODhhOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJMb3lfQmxvb2RBbmdlbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kODZjMmEyMGYwN2ZkMWM0NzI2Zjg1YzQ5MzY5Y2QzNzNhNDMyY2Y1MGIwODM0ZTU5ODljMmJmMWQxOThmMGU4IgogICAgfQogIH0KfQ"));

    private final String mockSkullItem_resultingTextureId =
        "d86c2a20f07fd1c4726f85c49369cd373a432cf50b0834e5989c2bf1d198f0e8";

    private final Optional<Skull> mockEntitySkull =
        Optional.of(Skull.ofTextureValue("ewogICJ0aW1lc3RhbXAiIDogMTcyMDA0MjMxNDE2MCwKICAicHJvZmlsZUlkIiA6ICJiNzRlYjViMTc5OTc0YzZjODk3ZTgwNTM4Y2M1NmYwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQYW5kYUNoYW4yOCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mZjdiYjk3NDgwYzEwZmRmZWI4ZmFlOTM5MjllNWVlYjRmMDQ5MWQ4Y2NhNzBlZDAwYjQ1ZTNmYjJiNzRmNmM1IgogICAgfQogIH0KfQ"));

    private final String mockEntitySkull_resultingTextureId =
        "ff7bb97480c10fdfeb8fae93929e5eeb4f0491d8cca70ed00b45e3fb2b74f6c5";

    private final Optional<Skull> mockBlockSkull =
        Optional.of(Skull.ofTextureValue("ewogICJ0aW1lc3RhbXAiIDogMTYxOTg1NTE0OTQ4NCwKICAicHJvZmlsZUlkIiA6ICJjZGM5MzQ0NDAzODM0ZDdkYmRmOWUyMmVjZmM5MzBiZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJSYXdMb2JzdGVycyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83ZmQ1ZjZiNmRiOTNkNTMwMjRmZWYyNzk4ZWI3ZThjYTQxMWEyYzZhZTEzMjA5YWQxM2UyOWZiOTgwZjE3ZjUiCiAgICB9CiAgfQp9"));

    private final String mockBlockSkull_resultingTextureId =
        "7fd5f6b6db93d53024fef2798eb7e8ca411a2c6ae13209ad13e29fb980f17f5";

    @Nested
    class shouldReturnEmpty {
        @Test
        public void whenCannotFindAny() {
            HoveredSkullItemFinderStub findHoveredSkullItem = new HoveredSkullItemFinderStub(Optional.empty());
            FacingEntitySkullFinderStub findFacingEntitySkull = new FacingEntitySkullFinderStub(Optional.empty());
            FacingBlockSkullFinderStub findFacingBlockSkull = new FacingBlockSkullFinderStub(Optional.empty());
            GetSkullIdHandler classUnderTest = new GetSkullIdHandler(findHoveredSkullItem, findFacingEntitySkull, findFacingBlockSkull);

            Optional<GetSkullIdHandler.GetSkullIdResponse> response = classUnderTest.getSkullId(new GetSkullIdHandler.GetSkullIdRequest());
            assertFalse(response.isPresent());
        }
    }

    @Nested
    class shouldReturnHoveredSkullItem {
        @Test
        public void whenFirstFoundSkullItem() {
            HoveredSkullItemFinderStub findHoveredSkullItem = new HoveredSkullItemFinderStub(mockSkullItem);
            FacingEntitySkullFinderStub findFacingEntitySkull = new FacingEntitySkullFinderStub(mockEntitySkull);
            FacingBlockSkullFinderStub findFacingBlockSkull = new FacingBlockSkullFinderStub(mockBlockSkull);
            GetSkullIdHandler classUnderTest = new GetSkullIdHandler(findHoveredSkullItem, findFacingEntitySkull, findFacingBlockSkull);

            Optional<GetSkullIdHandler.GetSkullIdResponse> response = classUnderTest.getSkullId(new GetSkullIdHandler.GetSkullIdRequest());

            assertTrue(response.isPresent());
            String textureId = response.get().textureId;
            assertEquals(mockSkullItem_resultingTextureId, textureId);

            assertEquals(1, findHoveredSkullItem.callCount);
            assertEquals(0, findFacingEntitySkull.callCount);
            assertEquals(0, findFacingBlockSkull.callCount);
        }
    }

    @Nested
    class shouldReturnFacingEntitySkull {
        @Test
        public void whenFirstFoundEntitySkull() {
            HoveredSkullItemFinderStub findHoveredSkullItem = new HoveredSkullItemFinderStub(Optional.empty());
            FacingEntitySkullFinderStub findFacingEntitySkull = new FacingEntitySkullFinderStub(mockEntitySkull);
            FacingBlockSkullFinderStub findFacingBlockSkull = new FacingBlockSkullFinderStub(mockBlockSkull);
            GetSkullIdHandler classUnderTest = new GetSkullIdHandler(findHoveredSkullItem, findFacingEntitySkull, findFacingBlockSkull);

            Optional<GetSkullIdHandler.GetSkullIdResponse> response = classUnderTest.getSkullId(new GetSkullIdHandler.GetSkullIdRequest());

            assertTrue(response.isPresent());
            String textureId = response.get().textureId;
            assertEquals(mockEntitySkull_resultingTextureId, textureId);

            assertEquals(1, findHoveredSkullItem.callCount);
            assertEquals(1, findFacingEntitySkull.callCount);
            assertEquals(0, findFacingBlockSkull.callCount);
        }
    }

    @Nested
    class shouldReturnFacingBlockSkull {
        @Test
        public void whenFirstFoundBlockSkull() {
            HoveredSkullItemFinderStub findHoveredSkullItem = new HoveredSkullItemFinderStub(Optional.empty());
            FacingEntitySkullFinderStub findFacingEntitySkull = new FacingEntitySkullFinderStub(Optional.empty());
            FacingBlockSkullFinderStub findFacingBlockSkull = new FacingBlockSkullFinderStub(mockBlockSkull);
            GetSkullIdHandler classUnderTest = new GetSkullIdHandler(findHoveredSkullItem, findFacingEntitySkull, findFacingBlockSkull);

            Optional<GetSkullIdHandler.GetSkullIdResponse> response = classUnderTest.getSkullId(new GetSkullIdHandler.GetSkullIdRequest());

            assertTrue(response.isPresent());
            String textureId = response.get().textureId;
            assertEquals(mockBlockSkull_resultingTextureId, textureId);

            assertEquals(1, findHoveredSkullItem.callCount);
            assertEquals(1, findFacingEntitySkull.callCount);
            assertEquals(1, findFacingBlockSkull.callCount);
        }
    }
}
