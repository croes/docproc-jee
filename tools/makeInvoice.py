#
#
#     Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
#
#     Licensed under the Apache License, Version 2.0 (the "License");
#     you may not use this file except in compliance with the License.
#     You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
#     Unless required by applicable law or agreed to in writing, software
#     distributed under the License is distributed on an "AS IS" BASIS,
#     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#     See the License for the specific language governing permissions and
#     limitations under the License.
#
#     Administrative Contact: dnet-project-office@cs.kuleuven.be
#     Technical Contact: bart.vanbrabant@cs.kuleuven.be
#

import sys;

print("invoice;date;name;street;zipcode;city;email;product;total")

for i in range(int(sys.argv[1])):
	print("2013;25/02/2013;Bart ;Celestijnenlaan 200A;3000;Leuven;bart@xx.eu;Cloud computing resource for 24/01/2013 to 24/02/2013;"+str(i*1342))
