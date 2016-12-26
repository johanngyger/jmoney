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

package name.gyger.jmoney.report;

public class Balance {

    private String accountName;
    private long balance;
    private boolean total;

    public Balance(String accountName, long balance, boolean total) {
        this.accountName = accountName;
        this.balance = balance;
        this.total = total;
    }

    public String getAccountName() {
        return accountName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isTotal() {
        return total;
    }

}
