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
from marvin.lib.base import (PhysicalNetwork, NetworkOffering, NiciraNvp)
from nose.plugins.attrib import attr
import time

class TestNiciraContoller(cloudstackTestCase):

    @classmethod
    def setUpClass(cls):
        test_case = super(TestNiciraContoller, cls)

        test_client    = test_case.getClsTestClient()
        cls.config     = test_case.getClsConfig()
        cls.api_client = test_client.getApiClient()

        cls.physical_networks = cls.config.zones[0].physical_networks
        cls.nicira_hosts     = cls.config.niciraNvp.hosts

        cls.physical_network_id = cls.get_nicira_enabled_physical_network_id(cls.physical_networks)

        cls.network_offerring_services = {
            'name':              'NiciraEnabledNetwork',
            'displaytext':       'NiciraEnabledNetwork',
            'guestiptype':       'Isolated',
            'supportedservices': 'SourceNat,Connectivity',
            'traffictype':       'GUEST',
            'availability':      'Optional',
            'serviceProviderList': {
                    'SourceNat':    'VirtualRouter',
                    'Connectivity': 'NiciraNvp'
            }
        }

        cls.network_offering = NetworkOffering.create(cls.api_client, cls.network_offerring_services)
        cls.network_offering.update(cls.api_client, state='Enabled')

        cls.nicir_credentials = {
            'username': 'admin',
            'password': 'admin'
        }

        cls.nicira_master_controller = cls.determine_master_controller(
            cls.nicira_hosts,
            cls.nicir_credentials
        )

        cls.transport_zone_uuid = cls.get_transport_zone_from_controller(
            cls.nicira_master_controller,
            cls.nicir_credentials
        )

        cls.cleanup = [
            cls.network_offering
        ]


    @classmethod
    def tearDownClass(cls):
        try:
            cleanup_resources(cls.api_client, cls.cleanup)
        except Exception as e:
            raise Exception("Warning: Exception during class cleanup : %s" % e)


    def tearDown(self):
        try:
            cleanup_resources(self.api_client, self.cleanup)
        except Exception as e:
            raise Exception("Warning: Exception during test cleanup : %s" % e)


    @classmethod
    def determine_master_controller(cls, hosts, credentials):
        for host in hosts:
            r1 = requests.post("https://%s/ws.v1/login" % host, credentials, verify=False)
            r2 = requests.get("https://%s/ws.v1/control-cluster/status" % host, verify=False, cookies=r1.cookies)
            status_code = r2.status_code
            if status_code == 401:
                continue
            elif status_code == 200:
                return host
        raise Exception("None of the supplied hosts (%s) is a Nicira controller" % hosts)


    @classmethod
    def get_transport_zone_from_controller(cls, controller_host, credentials):
        r1 = requests.post("https://%s/ws.v1/login" % controller_host, credentials, verify=False)
        r2 = requests.get("https://%s/ws.v1/transport-zone" % controller_host, verify=False, cookies=r1.cookies)
        status_code = r2.status_code
        if status_code == 200:
            list_transport_zone_response = r2.json()
            result_count = list_transport_zone_response['result_count']
            if result_count == 0:
                raise Exception('Nicira controller did not return any Transport Zones')
            elif result_count > 1:
                self.debug("Nicira controller returned %s Transport Zones, picking first one" % resultCount)
            transport_zone_api_url = list_transport_zone_response['results'][0]['_href']
            r3 = requests.get(
                "https://%s%s" % (controller_host, transport_zone_api_url),
                verify=False,
                cookies=r1.cookies
            )
            return r3.json()['uuid']
        else:
            raise Exception("Unexpected response from Nicira controller. Status code = %s, content = %s" % status_code)


    @classmethod
    def get_nicira_enabled_physical_network_id(cls, physical_networks):
        nicira_physical_network_name = None
        for physical_network in physical_networks:
            for provider in physical_network.providers:
                if provider.name == 'NiciraNvp':
                    nicira_physical_network_name = physical_network.name
        if nicira_physical_network_name is None:
            raise Exception('Did not find a Nicira enabled physical network in configuration')
        print "DEBUG:: >> nicira_physical_network_name = %s" % nicira_physical_network_name
        return PhysicalNetwork.list(cls.api_client, name=nicira_physical_network_name)[0].id


    def determine_slave_conroller(self, hosts, master_controller):
        slaves = [ s for s in hosts if s != master_controller ]
        if len(slaves) > 0:
            return slaves[0]
        else:
            raise Exception("None of the supplied hosts (%s) is a Nicira slave" % hosts)


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
        nicira_slave = self.determine_slave_conroller(self.nicira_hosts, self.nicira_master_controller)
        self.debug("Nicira slave controller is: %s " % nicira_slave)

        nicira_device = NiciraNvp.add(
            self.api_client,
            None,
            physical_network_id,
            hostname=nicira_slave,
            username=self.nicir_credentials['username'],
            password=self.nicir_credentials['password'],
            transportzoneid=self.transport_zone_uuid)
        # self.cleanup.append(nicira_device)
