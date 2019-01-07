curl -XDELETE https://search-personalcapital-ng2wweef7du74slalx62xmfqwy.us-west-2.es.amazonaws.com/csv_doc
curl -XPUT https://search-personalcapital-ng2wweef7du74slalx62xmfqwy.us-west-2.es.amazonaws.com/csv_doc -H 'Content-Type: application/json' -d @setup.json
