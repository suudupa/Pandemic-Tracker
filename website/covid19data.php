<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

$link = "https://www.worldometers.info/coronavirus/";

//get html content
$sites_html = file_get_contents($link);

//parse the html
$html = new DOMDocument();
@$html->loadHTML($sites_html);

$data = array(); //initialize array

$finder = new DomXPath($html); //query finder in html

//find the table of countries
$countries = $finder->query("//table[@id='main_table_countries_today']/tbody[1]/tr");

$count = $countries->length;

if ($count > 0) {
	foreach ($countries as $index => $country) {
		//check if table row is hidden in the table before parsing data to array
		if (strpos($country->getAttribute('style'), "display: none") === false) {

			$country = $finder->query('td', $country);
			$data[$country[1]->nodeValue]["cases"] = (trim($country[2]->nodeValue) !== "") ? (int) str_replace(',', '', $country[2]->nodeValue) : 0;
			$data[$country[1]->nodeValue]["cases_new"] = (trim($country[3]->nodeValue) !== "") ? (int) str_replace(',', '', $country[3]->nodeValue) : 0;
			$data[$country[1]->nodeValue]["deaths"] = (trim($country[4]->nodeValue) !== "") ? (int) str_replace(',', '', $country[4]->nodeValue) : 0;
			$data[$country[1]->nodeValue]["deaths_new"] = (trim($country[5]->nodeValue) !== "") ? (int) str_replace(',', '', $country[5]->nodeValue) : 0;
			$data[$country[1]->nodeValue]["recovered"] = (trim($country[6]->nodeValue) !== "") ? (int) str_replace(',', '', $country[6]->nodeValue) : 0;

		}

	}

	array_multisort(array_column($data, "cases"), SORT_DESC, $data);
	$data["last_updated"] = time();
	echo json_encode($data, JSON_PRETTY_PRINT);

	$fp = fopen(__DIR__ . '/covid19data.json', 'w');
	fwrite($fp, json_encode($data, JSON_PRETTY_PRINT));
	fclose($fp);
} else {
	$fp = fopen(__DIR__ . '/failed.json', 'w');
	fwrite($fp, json_encode($data, JSON_PRETTY_PRINT));
	fclose($fp);
}
?>