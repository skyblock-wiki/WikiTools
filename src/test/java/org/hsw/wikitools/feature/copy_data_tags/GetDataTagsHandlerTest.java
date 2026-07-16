package org.hsw.wikitools.feature.copy_data_tags;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GetDataTagsHandlerTest {
    private final Optional<ItemDataTags> mockHoveredItemDataTags = Optional.of(new ItemDataTags("{id:\"minecraft:nether_star\"}"));
    private final Optional<EntityDataTags> mockFacingEntityDataTags = Optional.of(new EntityDataTags("{Air:300s}", Optional.of("textures=[Property[name=textures, value=ewogIC, signature=yGPTZD]]")));
    private final Optional<EntityDataTags> mockFacingBlockDataTags = Optional.of(new EntityDataTags("{components:{}}", Optional.of("textures=[Property[name=textures, value=ewogIC, signature=bZUCtA]]")));

    @Nested
    class shouldReturnEmpty {
        @Test
        public void whenCannotFindAny() {
            HoveredItemDataTagsFinderStub findHoveredItemDataTags = new HoveredItemDataTagsFinderStub(Optional.empty());
            FacingEntityDataTagsFinderStub findFacingEntityDataTags = new FacingEntityDataTagsFinderStub(Optional.empty());
            FacingBlockDataTagsFinderStub findFacingBlockDataTags = new FacingBlockDataTagsFinderStub(Optional.empty());
            GetDataTagsHandler classUnderTest = new GetDataTagsHandler(findHoveredItemDataTags, findFacingEntityDataTags, findFacingBlockDataTags);

            Optional<GetDataTagsHandler.GetDataTagsResponse> response = classUnderTest.getDataTags(new GetDataTagsHandler.GetDataTagsRequest());
            assertFalse(response.isPresent());
        }
    }

    @Nested
    class shouldReturnHoveredItemDataTags {
        @Test
        public void whenFirstFoundItemDataTags() {
            HoveredItemDataTagsFinderStub findHoveredItemDataTags = new HoveredItemDataTagsFinderStub(mockHoveredItemDataTags);
            FacingEntityDataTagsFinderStub findFacingEntityDataTags = new FacingEntityDataTagsFinderStub(mockFacingEntityDataTags);
            FacingBlockDataTagsFinderStub findFacingBlockDataTags = new FacingBlockDataTagsFinderStub(mockFacingBlockDataTags);
            GetDataTagsHandler classUnderTest = new GetDataTagsHandler(findHoveredItemDataTags, findFacingEntityDataTags, findFacingBlockDataTags);

            Optional<GetDataTagsHandler.GetDataTagsResponse> response = classUnderTest.getDataTags(new GetDataTagsHandler.GetDataTagsRequest());

            assertTrue(response.isPresent());
            assertEquals("{id:\"minecraft:nether_star\"}", response.get().dataTags);

            assertEquals(1, findHoveredItemDataTags.callCount);
            assertEquals(0, findFacingEntityDataTags.callCount);
            assertEquals(0, findFacingBlockDataTags.callCount);
        }
    }

    @Nested
    class shouldReturnFacingEntityDataTags {
        @Test
        public void whenFirstFoundEntityDataTags() {
            HoveredItemDataTagsFinderStub findHoveredItemDataTags = new HoveredItemDataTagsFinderStub(Optional.empty());
            FacingEntityDataTagsFinderStub findFacingEntityDataTags = new FacingEntityDataTagsFinderStub(mockFacingEntityDataTags);
            FacingBlockDataTagsFinderStub findFacingBlockDataTags = new FacingBlockDataTagsFinderStub(mockFacingBlockDataTags);
            GetDataTagsHandler classUnderTest = new GetDataTagsHandler(findHoveredItemDataTags, findFacingEntityDataTags, findFacingBlockDataTags);

            Optional<GetDataTagsHandler.GetDataTagsResponse> response = classUnderTest.getDataTags(new GetDataTagsHandler.GetDataTagsRequest());

            assertTrue(response.isPresent());
            assertEquals("{Air:300s,__gameProfile:textures=[Property[name=textures, value=ewogIC, signature=yGPTZD]]}", response.get().dataTags);

            assertEquals(1, findHoveredItemDataTags.callCount);
            assertEquals(1, findFacingEntityDataTags.callCount);
            assertEquals(0, findFacingBlockDataTags.callCount);
        }
    }

    @Nested
    class shouldReturnFacingBlockDataTags {
        @Test
        public void whenFirstFoundBlockDataTags() {
            HoveredItemDataTagsFinderStub findHoveredItemDataTags = new HoveredItemDataTagsFinderStub(Optional.empty());
            FacingEntityDataTagsFinderStub findFacingEntityDataTags = new FacingEntityDataTagsFinderStub(Optional.empty());
            FacingBlockDataTagsFinderStub findFacingBlockDataTags = new FacingBlockDataTagsFinderStub(mockFacingBlockDataTags);
            GetDataTagsHandler classUnderTest = new GetDataTagsHandler(findHoveredItemDataTags, findFacingEntityDataTags, findFacingBlockDataTags);

            Optional<GetDataTagsHandler.GetDataTagsResponse> response = classUnderTest.getDataTags(new GetDataTagsHandler.GetDataTagsRequest());

            assertTrue(response.isPresent());
            assertEquals("{components:{},__gameProfile:textures=[Property[name=textures, value=ewogIC, signature=bZUCtA]]}", response.get().dataTags);

            assertEquals(1, findHoveredItemDataTags.callCount);
            assertEquals(1, findFacingEntityDataTags.callCount);
            assertEquals(1, findFacingBlockDataTags.callCount);
        }
    }
}
