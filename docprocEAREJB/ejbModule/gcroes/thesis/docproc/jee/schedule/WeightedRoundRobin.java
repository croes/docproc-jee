/**
*
* Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Administrative Contact: dnet-project-office@cs.kuleuven.be
* Technical Contact: bart.vanbrabant@cs.kuleuven.be
*/
package gcroes.thesis.docproc.jee.schedule;

import java.util.Arrays;

public class WeightedRoundRobin {

        private final float[] borders;
        private final float[] weights;
        private final String[] names;

        public WeightedRoundRobin(String[] names, float[] weights) {
                this.weights = weights;
                if (names.length != weights.length) {
                        throw new IllegalArgumentException("lengths do not match");
                }
                this.names = names;
                this.borders = new float[names.length];
                float sum = 0;
                for (int i = 0; i < weights.length; i++) {
                        sum += weights[i];
                }
                float runningsum = 0;
                for (int i = 0; i < weights.length; i++) {
                        runningsum += weights[i] / sum;
                        this.borders[i] = runningsum;
                }
        }
        
        public float[] getBorders() {
                return borders;
        }

        public String[] getNames() {
                return names;
        }
        
        /**
         * Get the weight at index
         */
        public float getWeight(int index) {
                assert(index < this.weights.length);
                
                return this.weights[index];
        }
        
        /**
         * Get the name at index
         */
        public String getName(int index) {
                assert(index < this.names.length);
                
                return this.names[index];
        }
        
        /**
         * Get the total length of the weights
         */
        public int getLength() {
                assert(this.weights.length == this.names.length);
                return this.weights.length;
        }

        /**
         * @return one of the registered names, the probability of any name being returned is proportional to the weight given
         */
        public String getNext(){
                if(names.length==0) {
                        return null;
                }
                int index = Arrays.binarySearch(borders, (float)Math.random());
                if(index<0){
                        return names[-index-1];
                }
                return names[index];
        }
}