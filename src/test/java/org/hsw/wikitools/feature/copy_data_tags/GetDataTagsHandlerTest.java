package org.hsw.wikitools.feature.copy_data_tags;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetDataTagsHandlerTest {
    private final String mockFacingEntityDataTags = EntityDataTags.getEntityDataTags("{Air:300s}", "textures=[Property[name=textures, value=ewogIC, signature=yGPTZD]]");
    private final String mockFacingBlockDataTags = EntityDataTags.getEntityDataTags("{components:{}}", "textures=[Property[name=textures, value=ewogIC, signature=bZUCtA]]");

    @Nested
    class shouldReturnEmpty {
        @Test
        public void whenCannotFindAny() {
            HoveredItemDataTagsFinderStub findHoveredItemDataTags = new HoveredItemDataTagsFinderStub(null);
            FacingEntityDataTagsFinderStub findFacingEntityDataTags = new FacingEntityDataTagsFinderStub(null);
            FacingBlockEntityDataTagsFinderStub findFacingBlockDataTags = new FacingBlockEntityDataTagsFinderStub(null);
            GetDataTagsHandler classUnderTest = new GetDataTagsHandler(findHoveredItemDataTags, findFacingEntityDataTags, findFacingBlockDataTags);

            String response = classUnderTest.getDataTags();
            assertNull(response);
        }
    }

    @Nested
    class shouldReturnHoveredItemDataTags {
        @Test
        public void whenFirstFoundItemDataTags() {
            String mockHoveredItemDataTags = "{id:\"minecraft:nether_star\"}";

            HoveredItemDataTagsFinderStub findHoveredItemDataTags = new HoveredItemDataTagsFinderStub(mockHoveredItemDataTags);
            FacingEntityDataTagsFinderStub findFacingEntityDataTags = new FacingEntityDataTagsFinderStub(mockFacingEntityDataTags);
            FacingBlockEntityDataTagsFinderStub findFacingBlockDataTags = new FacingBlockEntityDataTagsFinderStub(mockFacingBlockDataTags);
            GetDataTagsHandler classUnderTest = new GetDataTagsHandler(findHoveredItemDataTags, findFacingEntityDataTags, findFacingBlockDataTags);

            String dataTags = classUnderTest.getDataTags();

            assertNotNull(dataTags);
            assertEquals(mockHoveredItemDataTags, dataTags);

            assertEquals(1, findHoveredItemDataTags.callCount);
            assertEquals(0, findFacingEntityDataTags.callCount);
            assertEquals(0, findFacingBlockDataTags.callCount);
        }
    }

    @Nested
    class shouldReturnFacingEntityDataTags {
        @Test
        public void whenFirstFoundEntityDataTags() {
            HoveredItemDataTagsFinderStub findHoveredItemDataTags = new HoveredItemDataTagsFinderStub(null);
            FacingEntityDataTagsFinderStub findFacingEntityDataTags = new FacingEntityDataTagsFinderStub(mockFacingEntityDataTags);
            FacingBlockEntityDataTagsFinderStub findFacingBlockDataTags = new FacingBlockEntityDataTagsFinderStub(mockFacingBlockDataTags);
            GetDataTagsHandler classUnderTest = new GetDataTagsHandler(findHoveredItemDataTags, findFacingEntityDataTags, findFacingBlockDataTags);

            String dataTags = classUnderTest.getDataTags();

            assertNotNull(dataTags);
            assertEquals("{Air:300s,__gameProfile:textures=[Property[name=textures, value=ewogIC, signature=yGPTZD]]}", dataTags);

            assertEquals(1, findHoveredItemDataTags.callCount);
            assertEquals(1, findFacingEntityDataTags.callCount);
            assertEquals(0, findFacingBlockDataTags.callCount);
        }
    }

    @Nested
    class shouldReturnFacingBlockDataTags {
        @Test
        public void whenFirstFoundBlockDataTags() {
            HoveredItemDataTagsFinderStub findHoveredItemDataTags = new HoveredItemDataTagsFinderStub(null);
            FacingEntityDataTagsFinderStub findFacingEntityDataTags = new FacingEntityDataTagsFinderStub(null);
            FacingBlockEntityDataTagsFinderStub findFacingBlockDataTags = new FacingBlockEntityDataTagsFinderStub(mockFacingBlockDataTags);
            GetDataTagsHandler classUnderTest = new GetDataTagsHandler(findHoveredItemDataTags, findFacingEntityDataTags, findFacingBlockDataTags);

            String dataTags = classUnderTest.getDataTags();

            assertNotNull(dataTags);
            assertEquals("{components:{},__gameProfile:textures=[Property[name=textures, value=ewogIC, signature=bZUCtA]]}", dataTags);

            assertEquals(1, findHoveredItemDataTags.callCount);
            assertEquals(1, findFacingEntityDataTags.callCount);
            assertEquals(1, findFacingBlockDataTags.callCount);
        }
    }
}
