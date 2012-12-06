/*
 * Copyright 2012 Johann Gyger
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
 */

package name.gyger.jmoney.model;

import javax.persistence.Entity;

@Entity
public class SpecialCategory extends Category {

    private boolean isTransferCategory;

    private boolean isSplitCategory;

    public boolean isTransferCategory() {
        return isTransferCategory;
    }

    public void setTransferCategory(boolean transferCategory) {
        isTransferCategory = transferCategory;
    }

    public boolean isSplitCategory() {
        return isSplitCategory;
    }

    public void setSplitCategory(boolean splitCategory) {
        isSplitCategory = splitCategory;
    }

}
