package uy.com.fusion.fe.web.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.hazelcast.core.IMap;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

import uy.com.fusion.fe.web.api.document.DocumentEx;
import uy.com.fusion.fe.web.api.document.commons.ResponseListEx;
import uy.com.fusion.fe.web.persistence.cache.InputMapManager;

/**
 * Created by juanmartinez on 18/5/17.
 */
@Service
public class InputService {


    private static final int MAX_PAGE_SIZE = 20;
    @Autowired
    protected InputMapManager inputMapManager;


    public String addDocument(DocumentEx documentEx) {
        IMap<String, DocumentEx> map = this.inputMapManager.getMap();
        map.set(documentEx.getId(), documentEx);
        return documentEx.getId();
    }

    public DocumentEx getById(String id) {
        return this.inputMapManager.getMap().get(id);
    }

    public ResponseListEx getInputFilter(String name, String type, Integer offset, Integer limit) {
        Predicate<String, DocumentEx> predicate = filterInput(name, type, limit);

        IMap<String, DocumentEx> products = this.inputMapManager.getMap();

        try {
            List<DocumentEx> items = products.entrySet(predicate).stream().skip(offset).limit(limit).map(item -> item.getValue()).collect(Collectors.toList());
            return this.mapperResponseReservations(items, offset, limit, products.size());
        } catch (IndexOutOfBoundsException iobex) {
            String messageFormated = MessageFormat.format("The Limit value is incorrect: {0}, total: {1}", limit, products.size());
            throw new RestClientException(messageFormated);
        } catch (IllegalArgumentException iaex) {
            String messageFormated = MessageFormat.format("The Offset value is incorrect: {0}, total: {1}", offset, products.size());
            throw new RestClientException(messageFormated);
        }

    }

    private Predicate<String, DocumentEx> filterInput(String name, String type, int limit) {
        List<Predicate> predicates = new ArrayList<>();

        Predicate<String, DocumentEx> nonNullPredicate = Objects::nonNull;
        predicates.add(nonNullPredicate);

        Predicate<String, DocumentEx> nameNotNull = p -> p.getValue().getName() != null;
        predicates.add(nameNotNull);

        if (name != null) {
            Predicate<String, DocumentEx> statePredicate = p -> p.getValue().getName().equals(name);
            predicates.add(statePredicate);
        }
        Predicate<String, DocumentEx> typeNotNull = pd -> pd.getValue().getType() != null;
        predicates.add(typeNotNull);

        if (type != null) {
            Predicate<String, DocumentEx> typePredicate = p -> p.getValue().getType().equals(type);
            predicates.add(typePredicate);
        }

        Predicate[] arrayPredicate = new Predicate[predicates.size()];

        for (int i = 0; i < predicates.size(); i++) {
            arrayPredicate[i] = predicates.get(i);
        }

        Predicate<String, DocumentEx> fullPredicate = Predicates.and(arrayPredicate);
        if (limit == 0 || limit > MAX_PAGE_SIZE) {
            limit = MAX_PAGE_SIZE;
        }
        PagingPredicate pagingPredicate = new PagingPredicate(fullPredicate, limit);

        return pagingPredicate;
    }

    public ResponseListEx mapperResponseReservations(List<?> genericList, Integer offset, Integer limit, int total) {
        ResponseListEx response = new ResponseListEx();
        response.setItems(genericList);
        response.setOffset(offset);
        response.setLimit(limit);
        response.setTotal(total);
        return response;
    }
}
