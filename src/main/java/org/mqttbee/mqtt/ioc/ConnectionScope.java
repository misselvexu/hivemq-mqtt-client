/*
 * Copyright 2018 The MQTT Bee project
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
 */

package org.mqttbee.mqtt.ioc;

import javax.inject.Scope;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Scope for the lifetime of a single client connection.
 * <p>
 * {@code |<----------------------- Singleton ----------------------->|}
 * <p>
 * {@code ____|<------------------ ClientScope ------------------>|____}
 * <p>
 * {@code ______|<- ConnectionScope ->|____|<- ConnectionScope ->|_____}
 *
 * @author Silvio Giebl
 */
@Scope
@Documented
@Retention(RUNTIME)
public @interface ConnectionScope {

}
