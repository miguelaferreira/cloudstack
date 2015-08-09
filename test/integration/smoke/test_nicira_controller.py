#!/usr/bin/env python
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

import requests
from marvin.cloudstackTestCase import cloudstackTestCase
from marvin.lib.utils import cleanup_resources
from marvin.lib.common import get_zone
from nose.plugins.attrib import attr
import time
class TestNiciraContoller(cloudstackTestCase):

    @classmethod
    def setUpClass(cls):
        testClient   = super(TestNiciraContoller, cls).getClsTestClient()
        cls.apiclient    = testClient.getApiClient()
        cls.services     = testClient.getParsedTestDataConfig()
        cls.zone      = get_zone(cls.apiclient, testClient.getZoneForTests())
        # cls.niciraConfig = cls.services['niciraNvp']
        # cls.get_transport_zone_if_from_controller()
        # create net offring
        #     - VirtualRouter (nicira)
        #     - sourec nat (virtual router)
        cls.cleanup = []


    @classmethod
    def tearDownClass(cls):
        try:
            cleanup_resources(cls.apiclient, cls.cleanup)
        except Exception as e:
            raise Exception("Warning: Exception during cleanup : %s" % e)


    def setUp(self):
        self.apiclient = self.testClient.getApiClient()
        self.dbclient = self.testClient.getDbConnection()
        self.cleanup = []

    # def get_transport_zone_if_from_controller(cls):
    #     cls.niciraMaster = None
    #     niciraCredentials = {'username': cls.niciraConfig['username'], 'password': cls.niciraConfig['password']}
    #     for niciraHost in cls.niciraConfig['hosts']:
    #         r1 = requests.post("https://%s/ws.v1/login" % niciraHost, niciraCredentials, verify=False)
    #         r2 = requests.get("https://%s/ws.v1/transport-zone" % niciraHost, verify=False, cookies=r1.cookies)
    #         statusCode = r2.status_code
    #         if statusCode == 401:
    #             continue
    #         elif statusCode == 200:
    #             listTransportZoneResponse = r2.json()
    #             self.debug("Nicira master controller is: %s " % niciraHost)
    #             cls.niciraMaster = niciraHost
    #             response = r2.json()
    #             resultCount = response['result_count']
    #             if resultCount == 0:
    #                 raise Exception('Nicira controller did not return any Transport Zones')
    #             elif resultCount > 1:
    #                 self.debug("Nicira controller returned %s Transport Zones, picking first one" % resultCount)
    #             transportZoneApiUrl = listTransportZoneResponse['results'][0]['_href']
    #             r3 = requests.get("https://%s%s" % (niciraHost, transportZoneApiUrl), verify=False, cookies=r1.cookies)
    #             csl.transportZoneUuid = r3.json()['uuid']
    #         else:
    #             raise Exception("Unexpected response from Nicira controller. Status code = %s, content = %s" % statusCode)
    #     if cls.niciraMaster == None:
    #         raise Exception('Did not find a Nicira controller that is cluster master in config')


    @attr(tags = ["advanced", "smoke", "nicira"], required_hardware="true")
    def test_01_nicira(self):
        """
            Nicira clusters will redirect clients (in this case ACS) to the master node.
            This test assumes that a Nicira cluster is present and configured properly, and
            that it has at least two controller nodes. The test will check that ASC follows
            redirects by:
                - adding a Nicira Nvp device that points to one of the cluster's  controllers,
                - checking the device status,
                - replacing the Nicira Nvp device by another one that points to another controller,
                - and checking the device status
            If all is well, no matter what controller is specified in the Nicira Nvp device, status check
            should awyas succeed.
        """
        self.debug("Nicira config: %s " % cls.niciraConfig)
        raise Exception("I'm here")

        # nicira_physical_network_name = None
        # for physical_network in zone.physical_networks:
        #     for provider in physical_network.providers:
        #         if provider.name == 'NiciraNvp':
        #             nicira_physical_network_name = physical_network.name
        # if nicira_physical_network_name is None:
        #     raise Exception('Did not find a Nicira enabled physical network in configuration')
        # physicalNetworkId = PhysicalNetwork.list(cls.apiclient, {'name': nicira_physical_network_name})[0].id
        # niciraSlave = None
        # for niciraHost in cls.niciraConfig['hosts']:
        #     if niciraHost != cls.niciraMaster:
        #         niciraSlave = niciraHost
        # if niciraSlave == None:
        #     raise Exception('Cannot test controller redirect beacause there is no slave controller in config')
        # self.debug("Nicira slave controller is: %s " % niciraSlave)
        # niciraDevice = NiciraNvp.add(cls.apiclient, cls.niciraConfig, physicalNetworkId,
        #                        hostname=niciraSlave, transportzoneid=csl.transportZoneUuid)
        # cls.cleanup.append(niciraDevice)

    def determine_master():
        niciraCredentials = {'username': 'admin', 'password': 'admin'}
        hosts = ['nsxcon1', 'nsxcon2']
        for niciraHost in hosts:
            r1 = requests.post("https://%s/ws.v1/login" % niciraHost, niciraCredentials, verify=False)
            r2 = requests.get("https://%s/ws.v1/transport-zone" % niciraHost, verify=False, cookies=r1.cookies)
            statusCode = r2.status_code
            if statusCode == 401:
                continue
            elif statusCode == 200:
                listTransportZoneResponse = r2.json()
                response = r2.json()
                resultCount = response['result_count']
                if resultCount == 0:
                    raise Exception('Nicira controller did not return any Transport Zones')
                elif resultCount > 1:
                    print "Nicira controller returned %s Transport Zones, picking first one" % resultCount
                transportZoneApiUrl = listTransportZoneResponse['results'][0]['_href']
                r3 = requests.get("https://%s%s" % (niciraHost, transportZoneApiUrl), verify=False, cookies=r1.cookies)
                return (niciraHost, r3.json()['uuid'])

    #             listTransportZoneResponse = r2.json()
    #             self.debug("Nicira master controller is: %s " % niciraHost)
    #             cls.niciraMaster = niciraHost
    #             response = r2.json()
    #             resultCount = response['result_count']
    #             if resultCount == 0:
    #                 raise Exception('Nicira controller did not return any Transport Zones')
    #             elif resultCount > 1:
    #                 self.debug("Nicira controller returned %s Transport Zones, picking first one" % resultCount)
    #             transportZoneApiUrl = listTransportZoneResponse['results'][0]['_href']
    #             r3 = requests.get("https://%s%s" % (niciraHost, transportZoneApiUrl), verify=False, cookies=r1.cookies)
    #             csl.transportZoneUuid = r3.json()['uuid']
    #         else:
    #             raise Exception("Unexpected response from Nicira controller. Status code = %s, content = %s" % statusCode)

