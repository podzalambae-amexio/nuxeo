/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * Contributors:
 *     Salem Aouana
 */

package org.nuxeo.ecm.automation.core.operations.coldstorage;

import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.test.ColdStorageFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @since 11.1
 */
@RunWith(FeaturesRunner.class)
@Features(ColdStorageFeature.class)
@Deploy("org.nuxeo.ecm.automation.core")
public class MoveToColdStorageTest extends AbstractTestColdStorageOperation {

    @Inject
    protected CoreSession session;

    @Test
    public void shouldMoveToColdStorage() throws OperationException, IOException {
        DocumentModel documentModel = createFileDocument(session, true);
        moveContentToColdStorage(session, documentModel);
    }

    @Test
    public void shouldFailWhenMovingDocumentBlobAlreadyInColdStorage() throws OperationException, IOException {
        DocumentModel documentModel = createFileDocument(session, true);
        // make a move
        moveContentToColdStorage(session, documentModel);
        try {
            // try to make a second move
            moveContentToColdStorage(session, documentModel);
            fail("Should fail because the content is already in cold storage");
        } catch (NuxeoException ne) {
            assertEquals(SC_CONFLICT, ne.getStatusCode());
        }
    }

    @Test
    public void shouldFailWhenMovingToColdStorageDocumentWithoutContent() throws OperationException, IOException {
        DocumentModel documentModel = createFileDocument(session, false);
        try {
            moveContentToColdStorage(session, documentModel);
            fail("Should fail because there is no main content associated with the document");
        } catch (NuxeoException ne) {
            assertEquals(SC_NOT_FOUND, ne.getStatusCode());
        }
    }
}
