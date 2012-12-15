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

package name.gyger.jmoney.dto;

import name.gyger.jmoney.model.Entry;

import java.util.ArrayList;
import java.util.List;

public class EntryDetailsDto extends EntryDto {

    private List<SubEntryDto> subEntries;

    public EntryDetailsDto() {
    }

    public EntryDetailsDto(Entry e) {
        super(e);

        List<Entry> ses = e.getSubEntries();
        if (ses != null && ses.size() > 0) {
            subEntries = new ArrayList<SubEntryDto>();
            for (Entry se : ses) {
                subEntries.add(new SubEntryDto(se));
            }
        }
    }

    public void mapToModel(Entry entry) {
        super.mapToModel(entry);
    }

    public List<SubEntryDto> getSubEntries() {
        return subEntries;
    }

    public void setSubEntries(List<SubEntryDto> subEntries) {
        this.subEntries = subEntries;
    }

}
